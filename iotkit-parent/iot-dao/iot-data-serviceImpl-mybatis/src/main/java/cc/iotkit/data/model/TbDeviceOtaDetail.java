package cc.iotkit.data.model;

import cc.iotkit.model.ota.DeviceOtaDetail;
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
@TableName("device_ota_detail")
@ApiModel(value = "设备升级明细")
@AutoMapper(target = DeviceOtaDetail.class)
public class TbDeviceOtaDetail {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Integer step;

    private String taskId;

    @TableField("desc")
    private String desc;

    private String version;

    private String module;

    private String deviceId;

    private String productKey;

    private String deviceName;

    private Long otaInfoId;
}
