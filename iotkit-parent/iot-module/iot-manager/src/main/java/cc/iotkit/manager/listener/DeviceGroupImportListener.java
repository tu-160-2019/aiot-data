package cc.iotkit.manager.listener;

import cc.iotkit.common.excel.core.ExcelListener;
import cc.iotkit.common.excel.core.ExcelResult;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.SpringUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceGroupBo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupImportVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupVo;
import cc.iotkit.manager.service.IDeviceManagerService;
import cc.iotkit.model.device.DeviceGroup;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class DeviceGroupImportListener extends AnalysisEventListener<DeviceGroupImportVo> implements ExcelListener<DeviceGroupImportVo> {

    private final Boolean isUpdateSupport;

    private int successNum = 0;
    private int failureNum = 0;

    private final String userId;

    private IDeviceManagerService deviceManagerService;

    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public DeviceGroupImportListener(Boolean isUpdateSupport) {
        this.isUpdateSupport = isUpdateSupport;
        this.userId = String.valueOf(LoginHelper.getUserId());
        this.deviceManagerService = SpringUtils.getBean(IDeviceManagerService.class);
    }

    @Override
    public ExcelResult<DeviceGroupImportVo> getExcelResult() {

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
            public List<DeviceGroupImportVo> getList() {
                return Collections.emptyList();
            }

            @Override
            public List<String> getErrorList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public void invoke(DeviceGroupImportVo deviceGroupImportVo, AnalysisContext analysisContext) {
        if ( StringUtils.isEmpty(deviceGroupImportVo.getId()) || StringUtils.isEmpty(deviceGroupImportVo.getName()) ) {
            failureNum++;
            failureMsg.append("<br/>第").append(analysisContext.getCurrentRowNum()).append("行，设备分组ID或名称不能为空");
            return;
        }

        DeviceGroupVo deviceGroupVo = this.deviceManagerService.getDeviceGroup(deviceGroupImportVo.getId());
        try {
            if (ObjectUtil.isNull(deviceGroupVo)) {
                // 新增
                DeviceGroup deviceGroup = BeanUtil.toBean(deviceGroupImportVo, DeviceGroup.class);
                deviceGroup.setUid(this.userId);
                this.deviceManagerService.addGroup(deviceGroup);
            } else if (Boolean.TRUE.equals(isUpdateSupport)) {
                // 修改
                DeviceGroupBo deviceGroupBo = BeanUtil.toBean(deviceGroupImportVo, DeviceGroupBo.class);
                deviceGroupBo.setUid(this.userId);
                this.deviceManagerService.updateGroup(deviceGroupBo);
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
