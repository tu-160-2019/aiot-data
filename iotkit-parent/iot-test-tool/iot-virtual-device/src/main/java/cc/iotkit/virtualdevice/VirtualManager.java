/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.virtualdevice;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.thing.ThingService;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IVirtualDeviceData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.VirtualDevice;
import cc.iotkit.model.device.VirtualDeviceLog;
import cc.iotkit.mq.MqProducer;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.script.IScriptEngine;
import cc.iotkit.script.ScriptEngineFactory;
import cc.iotkit.temporal.IVirtualDeviceLogData;
import cc.iotkit.virtualdevice.trigger.RandomScheduleBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class VirtualManager {
    private final Map<String, IScriptEngine> virtualScripts = new HashMap<>();
    private final Map<String, Set<String>> deviceIdToVirtualId = new HashMap<>();

    @Autowired
    private IVirtualDeviceData virtualDeviceData;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private IVirtualDeviceLogData virtualDeviceLogData;
    @Autowired
    private IThingService thingService;
    @Autowired
    private MqProducer<ThingModelMessage> producer;

    public VirtualManager(){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(this::init, 8, TimeUnit.SECONDS);
    }

    public void init() {
        List<VirtualDevice> virtualDevices = getAllVirtualDevices();
        for (VirtualDevice virtualDevice : virtualDevices) {
            addTask(virtualDevice);
        }
    }

    /**
     * 判断设备是否应用了虚拟设备
     */
    public boolean isVirtual(String deviceId) {
        return deviceIdToVirtualId.containsKey(deviceId);
    }

    /**
     * 调用虚拟设备下发
     */
    @SneakyThrows
    public void send(ThingService<?> service) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceName(service.getDeviceName());
        String deviceId = deviceInfo.getDeviceId();

        //根据设备Id取虚拟设备列表
        Set<String> virtualIds = deviceIdToVirtualId.get(deviceId);
        for (String virtualId : virtualIds) {
            IScriptEngine scriptEngine = virtualScripts.get(virtualId);
            //多条虚拟设备消息
            List<ThingModelMessage> result = scriptEngine.invokeMethod(
                    new TypeReference<>() {
                    },
                    "receive", service);
            for (ThingModelMessage msg : result) {
                processReport(msg);
            }
            log.info("virtual device send result:{}", JsonUtils.toJsonString(result));
        }
    }

    /**
     * 添加虚拟设备
     */
    public void add(VirtualDevice virtualDevice) {
        addTask(virtualDevice);
    }

    /**
     * 删除虚拟设备
     */
    public void remove(VirtualDevice virtualDevice) {
        deleteTask(virtualDevice);
    }

    /**
     * 立即执行一次虚拟设备上报
     */
    public void run(VirtualDevice virtualDevice) {
        List<String> devices = virtualDevice.getDevices();
        VirtualDeviceLog virtualDeviceLog = VirtualDeviceLog.builder()
                .id(UUID.randomUUID().toString())
                .virtualDeviceId(virtualDevice.getId())
                .virtualDeviceName(virtualDevice.getName())
                .deviceTotal(devices.size())
                .result("success")
                .logAt(System.currentTimeMillis())
                .build();
        try {
            IScriptEngine scriptEngine = virtualScripts.get(virtualDevice.getId());
            if ( null == scriptEngine ) {
                //添加新的脚本对象
                scriptEngine = ScriptEngineFactory.getScriptEngine("js");
                scriptEngine.setScript(virtualDevice.getScript());
                virtualScripts.put(virtualDevice.getId(), scriptEngine);
            }
            for (String deviceId : devices) {
                DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
                processReport(scriptEngine.invokeMethod(new TypeReference<>() {
                }, "report", device));
            }
        } catch (Throwable e) {
            virtualDeviceLog.setResult(e.getMessage());
            log.error("run VirtualDevice error", e);
        }
        virtualDeviceLogData.add(virtualDeviceLog);
    }

    /**
     * 更新虚拟设备
     */
    public void update(VirtualDevice virtualDevice) {
        remove(virtualDevice);
        add(virtualDevice);
    }

    /**
     * 获取所有虚拟设备
     */
    private List<VirtualDevice> getAllVirtualDevices() {
        List<VirtualDevice> randomVirtualDevices = virtualDeviceData
                .findByTriggerAndState(VirtualDevice.TRIGGER_RANDOM, VirtualDevice.STATE_RUNNING);
        List<VirtualDevice> cronVirtualDevices = virtualDeviceData
                .findByTriggerAndState(VirtualDevice.TRIGGER_CRON, VirtualDevice.STATE_RUNNING);
        cronVirtualDevices.addAll(randomVirtualDevices);
        return cronVirtualDevices;
    }

    private void addTask(VirtualDevice virtualDevice) {
        try {
            String id = virtualDevice.getId();
            String name = virtualDevice.getName();
            String script = virtualDevice.getScript();
            log.info("adding virtual device job,id:{},name:{}", id, name);

            //添加新的脚本对象
            IScriptEngine scriptEngine = ScriptEngineFactory.getScriptEngine("js");
            scriptEngine.setScript(script);
            virtualScripts.put(id, scriptEngine);
            List<DeviceInfo> devices = new ArrayList<>();
            for (String deviceId : virtualDevice.getDevices()) {
                devices.add(deviceInfoData.findByDeviceId(deviceId));
                //更新deviceId的虚拟设备Id对应关系
                Set<String> virtualIds = deviceIdToVirtualId.getOrDefault(deviceId, new HashSet<>());
                virtualIds.add(id);
                //一个真实设备可能会被多个虚拟设备使用
                deviceIdToVirtualId.put(deviceId, virtualIds);
            }

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("virtualManager", this);
            jobDataMap.put("virtualDevice", virtualDevice);
            jobDataMap.put("devices", devices);

            JobDetail jobDetail = JobBuilder.newJob(VirtualExecutor.class)
                    .withIdentity(id, name)
                    .usingJobData(jobDataMap)
                    .build();

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger_" + id, "triggerGroup_" + name)
                    .startNow()
                    .withSchedule(
                            getTriggerBuilder(virtualDevice)
                    ).build();

            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Throwable e) {
            log.error("create job failed", e);
        }
    }

    private ScheduleBuilder<?> getTriggerBuilder(VirtualDevice virtualDevice) {
        String type = virtualDevice.getTrigger();
        if ("random".equals(type)) {
            return new RandomScheduleBuilder(virtualDevice.getTriggerExpression());
        }
        if ("cron".equals(type)) {
            return CronScheduleBuilder.cronSchedule(virtualDevice.getTriggerExpression());
        }
        return null;
    }

    @SneakyThrows
    public void deleteTask(VirtualDevice virtualDevice) {
        String id = virtualDevice.getId();
        String name = virtualDevice.getName();

        //删除脚本对象
        virtualScripts.remove(id);

        //更新deviceId的虚拟设备Id对应关系
        Iterator<String> keyIterator = deviceIdToVirtualId.keySet().iterator();
        while (keyIterator.hasNext()) {
            String deviceId = keyIterator.next();
            Set<String> virtualIds = deviceIdToVirtualId.get(deviceId);
            virtualIds.remove(id);
            if (virtualIds.size() == 0) {
                keyIterator.remove();
            }
        }

        //删除job
        TriggerKey triggerKey = new TriggerKey("trigger_" + id, "triggerGroup_" + name);
        if (!scheduler.checkExists(triggerKey)) {
            return;
        }
        scheduler.deleteJob(JobKey.jobKey(id, name));
    }

    /**
     * 处理js上报方法返回结果
     */
    private void processReport(ThingModelMessage message) {
        try {
            producer.publish(Constants.THING_MODEL_MESSAGE_TOPIC, message);
        } catch (Throwable e) {
            log.error("process js data error", e);
        }
    }

    /**
     * 调用脚本中上报方法
     */
    @SneakyThrows
    public void invokeReport(DeviceInfo device, String virtualId) {
        //设备上线
        deviceOnline(device);

        IScriptEngine scriptEngine = virtualScripts.get(virtualId);
        if (scriptEngine == null) {
            return;
        }
        processReport(scriptEngine.invokeMethod(new TypeReference<>() {
        }, "report", device));
    }

    /**
     * 设备上线
     */
    private void deviceOnline(DeviceInfo device) {
        DeviceInfo.State state = device.getState();
        if (state == null || !state.isOnline()) {
            //设备离线，产生上线消息
            thingService.post("NONE", DeviceStateChange.builder()
                    .id(UniqueIdUtil.newRequestId())
                    .productKey(device.getProductKey())
                    .deviceName(device.getDeviceName())
                    .state(DeviceState.ONLINE)
                    .time(System.currentTimeMillis())
                    .build());
        }
    }

    /**
     * 保存虚拟设备日志
     */
    public void saveLog(VirtualDeviceLog log) {
        virtualDeviceLogData.add(log);
    }

}
