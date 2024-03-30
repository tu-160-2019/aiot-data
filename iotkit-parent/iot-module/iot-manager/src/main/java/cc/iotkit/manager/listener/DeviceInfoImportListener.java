package cc.iotkit.manager.listener;

import cc.iotkit.common.excel.core.ExcelListener;
import cc.iotkit.common.excel.core.ExcelResult;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.SpringUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.manager.dto.bo.device.DeviceInfoBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceAddGroupBo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoImportVo;
import cc.iotkit.manager.service.IDeviceManagerService;
import cc.iotkit.model.device.DeviceInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceInfoImportListener extends AnalysisEventListener<DeviceInfoImportVo> implements ExcelListener<DeviceInfoImportVo> {

    private Boolean isUpdateSupport;

    private int successNum = 0;
    private int failureNum = 0;

    private final String userId;

    private IDeviceManagerService deviceManagerService;

    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public DeviceInfoImportListener(Boolean isUpdateSupport) {
        this.isUpdateSupport = isUpdateSupport;
        this.userId = String.valueOf(LoginHelper.getUserId());
        this.deviceManagerService = SpringUtils.getBean(IDeviceManagerService.class);
    }

    @Override
    public ExcelResult<DeviceInfoImportVo> getExcelResult() {
        return new ExcelResult<>() {
            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new BizException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条");
                }
                return successMsg.toString();
            }

            @Override
            public List<DeviceInfoImportVo> getList() {
                return Collections.emptyList();
            }

            @Override
            public List<String> getErrorList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public void invoke(DeviceInfoImportVo deviceInfoImportVo, AnalysisContext analysisContext) {
        if (StringUtils.isBlank(deviceInfoImportVo.getProductKey())) {
            failureMsg.append("<br/>第").append(analysisContext.getCurrentRowNum()).append("行，产品key不能为空！");
            failureNum++;
        }
        if (StringUtils.isBlank(deviceInfoImportVo.getDeviceName())) {
            failureMsg.append("<br/>第").append(analysisContext.getCurrentRowNum()).append("行，设备名称不能为空！");
            failureNum++;
        }
        DeviceInfo deviceInfo = this.deviceManagerService.getByPkDn(deviceInfoImportVo.getProductKey(), deviceInfoImportVo.getDeviceName());
        try {
            // todo 处理groups
            if(ObjectUtil.isNull(deviceInfo)) {
                // 新增
                DeviceInfoBo deviceInfoBo = BeanUtil.toBean(deviceInfoImportVo, DeviceInfoBo.class);
                this.deviceManagerService.addDevice(deviceInfoBo);
                // 新增后重新获取deviceInfo， 此操作冗余，是建立在不修改源码基础上的处理方式，可优化
                deviceInfo = this.deviceManagerService.getByPkDn(deviceInfoImportVo.getProductKey(), deviceInfoImportVo.getDeviceName());
            } else if (Boolean.TRUE.equals(isUpdateSupport)) {
                // 修改
                DeviceInfoBo deviceInfoBo = BeanUtil.toBean(deviceInfo, DeviceInfoBo.class);
                this.deviceManagerService.saveDevice(deviceInfoBo);
            }

            // 设备分组处理
            String group = deviceInfoImportVo.getDeviceGroup();
            if (StringUtils.isNotBlank(group)) {
                DeviceAddGroupBo deviceAddGroupBo = new DeviceAddGroupBo();
                deviceAddGroupBo.setGroup(group);
                String deviceId = deviceInfo.getDeviceId();
                ArrayList<String> devices = new ArrayList<>();
                devices.add(deviceId);
                deviceAddGroupBo.setDevices(devices);
                this.deviceManagerService.addDevice2Group(deviceAddGroupBo);
            }

            successNum++;
        } catch (Exception e) {
            failureNum++;
            failureMsg.append("<br/>第").append(analysisContext.getCurrentRowNum()).append("行，导入失败:").append(e.getMessage());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
