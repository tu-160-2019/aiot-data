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

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.VirtualDevice;
import cc.iotkit.model.device.VirtualDeviceLog;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class VirtualExecutor implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        Map<String, Object> data = context.getMergedJobDataMap();
        VirtualManager virtualManager = (VirtualManager) data.get("virtualManager");
        VirtualDevice virtualDevice = (VirtualDevice) data.get("virtualDevice");
        List<DeviceInfo> devices = (List<DeviceInfo>) data.get("devices");
        devices = devices == null ? new ArrayList<>() : devices;
        JobDetail jobDetail = context.getJobDetail();
        String jobKey = jobDetail.getKey().toString();

        VirtualDeviceLog virtualDeviceLog = VirtualDeviceLog.builder()
                .id(UUID.randomUUID().toString())
                .virtualDeviceId(virtualDevice.getId())
                .virtualDeviceName(virtualDevice.getName())
                .deviceTotal(devices.size())
                .result("success")
                .logAt(System.currentTimeMillis())
                .build();

        try {
            for (DeviceInfo device : devices) {
                if (device == null) {
                    continue;
                }
                log.info("invoke virtual device report,jobKey:{},deviceId:{}", jobKey, device.getDeviceId());
                virtualManager.invokeReport(device, virtualDevice.getId());
            }
        } catch (Throwable e) {
            virtualDeviceLog.setResult(e.getMessage());
            log.error("execute job error", e);
        }
        virtualManager.saveLog(virtualDeviceLog);
    }
}
