package cc.iotkit.data.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_group_mapping")
public class TbDeviceGroupMapping {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "设备组映射id")
    private String id;

    @ApiModelProperty(value = "设备id")
    private String deviceId;

    @ApiModelProperty(value = "设备组id")
    private String groupId;

}
