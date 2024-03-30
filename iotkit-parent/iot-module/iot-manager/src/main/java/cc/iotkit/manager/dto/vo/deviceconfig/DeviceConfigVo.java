package cc.iotkit.manager.dto.vo.deviceconfig;

import cc.iotkit.model.device.DeviceConfig;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel(value = "DeviceConfigVo")
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = DeviceConfig.class)

public class DeviceConfigVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "设备配置id")
    @ExcelProperty(value = "设备配置id")
    private String id;

    @ApiModelProperty(value = "设备配置json内容")
    @ExcelProperty(value = "设备配置json内容")
    private String config;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Long createAt;

    @ApiModelProperty(value = "设备id")
    @ExcelProperty(value = "设备id")
    private String deviceId;

    @ApiModelProperty(value = "设备名称")
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "产品key")
    @ExcelProperty(value = "产品key")
    private String productKey;


}
