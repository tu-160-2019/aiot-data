package cc.iotkit.manager.dto.vo.devicegroup;

import cc.iotkit.model.device.DeviceGroup;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AutoMapper(target = DeviceGroup.class, reverseConvertGenerate = false)
public class DeviceGroupImportVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ExcelProperty(value = "设备组id")
    private String id;

    @ExcelProperty(value = "设备组名称")
    private String name;

    @ExcelProperty(value = "分组说明")
    private String remark;
}
