package cc.iotkit.model.system;

import cc.iotkit.model.Id;
import cc.iotkit.model.TenantModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author Lion Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysOss  extends TenantModel implements Id<Long>,Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 上传人名称
     */
    private String createByName;

    /**
     * 服务商
     */
    private String service;


}
