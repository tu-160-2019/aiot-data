package cc.iotkit.manager.dto.bo.ota;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.ota.OtaPackage;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: 石恒
 * @Date: 2023/6/27 22:09
 * @Description:
 */
@ApiModel(value = "OtaPackageBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = OtaPackage.class, reverseConvertGenerate = false)
public class OtaPackageBo extends BaseDto {

    /**
     * 文件包大小
     */
    private Long size;

    /**
     * 签名
     */
    private String sign;

    /**
     * 是否差分升级
     */
    private Boolean isDiff;

    /**
     * 文件MD5后的值
     */
    private String md5;

    /**
     * 包名
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

    /**
     * 版本
     */
    private String version;

    /**
     * 升级包地址
     */
    private String url;

    /**
     * 签名方式
     */
    private String signMethod;

    /**
     * 模块
     */
    private String module;

    /**
     * 扩展数据
     */
    private String extData;
    /**
     * 产品key
     */
    private String productKey;
}
