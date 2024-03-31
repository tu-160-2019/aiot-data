package cc.iotkit.data.model;

import cc.iotkit.model.ota.DeviceOtaInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:22
 * @Description:
 */
@Data
@TableName("device_ota_info")
@ApiModel(value = "设备信息")
@AutoMapper(target = DeviceOtaInfo.class)
public class TbDeviceOtaInfo {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long packageId;

    @TableField("desc")
    private String desc;

    private String version;

    private String module;

    private Integer total;

    private Integer success;

    private Integer fail;

    private String productKey;

    private Long createAt;
}
