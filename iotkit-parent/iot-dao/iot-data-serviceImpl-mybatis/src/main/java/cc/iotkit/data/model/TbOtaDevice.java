package cc.iotkit.data.model;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Author: 石恒
 * @Date: 2023/5/25 23:26
 * @Description:
 */
@Data
@TableName("ota_device")
public class TbOtaDevice {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String deviceName;

    private String deviceId;

    private String version;

    private Integer status;

    private Long createAt;
}
