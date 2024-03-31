package cc.iotkit.data.model;

import cc.iotkit.model.device.DeviceConfig;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.JdbcType;

@Data
@ApiModel(value = "设备配置")
@TableName("device_config")
@AutoMapper(target = DeviceConfig.class)
public class TbDeviceConfig {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "设备配置id")
    private String id;

    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 产品key
     */
    @ApiModelProperty(value = "产品key")
    private String productKey;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备配置json内容
     */
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    @ApiModelProperty(value = "设备配置json内容")
    private String config;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
