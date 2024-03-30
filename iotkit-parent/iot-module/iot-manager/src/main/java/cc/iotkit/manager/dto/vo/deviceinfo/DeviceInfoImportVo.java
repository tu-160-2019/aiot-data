package cc.iotkit.manager.dto.vo.deviceinfo;

import cc.iotkit.model.device.DeviceInfo;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serializable;


@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = DeviceInfo.class,convertGenerate = false)
public class DeviceInfoImportVo implements Serializable {
    private static final long serialVersionUID = -1L;

    @ExcelProperty(value = "设备名称")
    private String deviceName;

    @ExcelProperty(value = "设备型号")
    private String model;

    @ExcelProperty(value = "父级id")
    private String parentId;

    @ExcelProperty(value = "产品key")
    private String productKey;

    @ExcelProperty(value = "设备分组")
    private String deviceGroup;
}
