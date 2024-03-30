package cc.iotkit.plugins.modbus.service;

import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.script.IScriptEngine;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.core.PluginCloseType;
import com.gitee.starblues.core.PluginInfo;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author sjg
 */
@Slf4j
@Service
public class ModbusPlugin implements PluginCloseListener {

    @Autowired
    private PluginInfo pluginInfo;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginScript pluginScript;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    private IScriptEngine scriptEngine;

    private final ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder("localhost").setPort(502).build();
    private ModbusTcpMaster master;

    private final Set<Integer> registeredDevice = new HashSet<>();

    private final int[] slaves = new int[]{1, 2, 3};

    private final Map<Integer, String> DATA_CACHE = new HashMap<>();

    @PostConstruct
    public void init() {
        master = new ModbusTcpMaster(config);
        CompletableFuture<ModbusTcpMaster> connect = master.connect();
        connect.thenAccept(modbusTcpMaster -> System.out.println("111:" + modbusTcpMaster.getConfig()));

        //获取脚本引擎
        scriptEngine = pluginScript.getScriptEngine(pluginInfo.getPluginId());
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 1000)
    public void taskRead() {
        for (int slave : slaves) {
            CompletableFuture<ReadHoldingRegistersResponse> future =
                    master.sendRequest(new ReadHoldingRegistersRequest(0, 10), slave);

            future.thenAccept(response -> {
                String rspBytes = ByteBufUtil.hexDump(response.getRegisters());
                ReferenceCountUtil.release(response);

                log.info("receive Response: " + rspBytes);
                //相同数据不处理
                if (DATA_CACHE.getOrDefault(slave, "").equals(rspBytes)) {
                    return;
                }
                DATA_CACHE.put(slave, rspBytes);

                if (!registeredDevice.contains(slave)) {
                    //第一次读取自动注册设备
                    thingService.post(pluginInfo.getPluginId(), DeviceRegister.builder()
                            .id(UUID.randomUUID().toString())
                            .productKey("cGCrkK7Ex4FESAwe")
                            .deviceName(String.format("modbus_%d", slave))
                            .build());
                    registeredDevice.add(slave);
                    //并上线
                    thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                            .id(UUID.randomUUID().toString())
                            .productKey("cGCrkK7Ex4FESAwe")
                            .deviceName(String.format("modbus_%d", slave))
                            .state(DeviceState.ONLINE)
                            .build());
                }

                //调用脚本解码
                Map<String, Object> rst = scriptEngine.invokeMethod(new TypeReference<>() {
                }, "decode", rspBytes);
                if (rst == null || rst.isEmpty()) {
                    return;
                }

                //属性上报
                thingService.post(pluginInfo.getPluginId(), PropertyReport.builder()
                        .id(UUID.randomUUID().toString())
                        .productKey("cGCrkK7Ex4FESAwe")
                        .deviceName(String.format("modbus_%d", slave))
                        .params(rst)
                        .build());
            });
        }
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            log.info("plugin close,type:{},pluginId:{}", closeType, pluginInfo.getPluginId());
            master.disconnect();
        } catch (Throwable e) {
            log.error("modbus plugin stop error", e);
        }
    }

}
