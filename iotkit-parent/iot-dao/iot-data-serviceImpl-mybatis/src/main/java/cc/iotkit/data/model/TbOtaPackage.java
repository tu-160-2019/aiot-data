package cc.iotkit.data.model;

import cc.iotkit.model.ota.OtaPackage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @Author: 石恒
 * @Date: 2023/5/19 21:25
 * @Description:
 */
@Data
@TableName("ota_package")
@AutoMapper(target = OtaPackage.class)
public class TbOtaPackage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long size;

    private String sign;

    private Boolean isDiff;

    private String md5;

    private String name;

    @TableField("`desc`")
    private String desc;

    private String version;

    private String url;

    private String signMethod;

    private String module;

    private String productKey;

    private String extData;

    private Long createAt;
}
