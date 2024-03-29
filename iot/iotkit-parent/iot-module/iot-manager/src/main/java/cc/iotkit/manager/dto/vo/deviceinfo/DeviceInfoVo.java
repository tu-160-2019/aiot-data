package cc.iotkit.manager.dto.vo.deviceinfo;

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;


@ApiModel(value = "DeviceInfoVo")
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = DeviceInfo.class,convertGenerate = false)
public class DeviceInfoVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "")
    @ExcelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Long createAt;

    @ApiModelProperty(value = "设备id")
    @ExcelProperty(value = "设备id")
    private String deviceId;

    @ApiModelProperty(value = "设备名称")
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    @ExcelProperty(value = "设备类型")
    private String model;

    @ApiModelProperty(value = "设备离线时间")
    @ExcelProperty(value = "设备离线时间")
    @ReverseAutoMapping(source = "state.offlineTime", target = "offlineTime")
    @AutoMapping(ignore = true)
    private Long offlineTime;

    @ApiModelProperty(value = "设备在线时间")
    @ExcelProperty(value = "设备在线时间")
    @ReverseAutoMapping(source = "state.onlineTime", target = "onlineTime")
    @AutoMapping(ignore = true)
    private Long onlineTime;

    @ApiModelProperty(value = "父级id")
    @ExcelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "产品key")
    @ExcelProperty(value = "产品key")
    private String productKey;

    @ApiModelProperty(value = "设备描述")
    @ExcelProperty(value = "设备描述")
    private String secret;

    @ApiModelProperty(value = "设备状态")
    @ExcelProperty(value = "设备状态")
    @ReverseAutoMapping(source = "state.online", target = "online")
    @AutoMapping(ignore = true)
    private Boolean online;

    @ApiModelProperty(value = "所属产品信息")
    private Product product;

    @ApiModelProperty(value = "所属分组")
    private Map<String, DeviceInfo.Group> group;

}
