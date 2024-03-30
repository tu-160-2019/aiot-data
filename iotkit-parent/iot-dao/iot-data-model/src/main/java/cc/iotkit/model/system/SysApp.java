package cc.iotkit.model.system;

import cc.iotkit.model.Id;
import cc.iotkit.model.TenantModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 对象 SYS_APP
 *
 * @author tfd
 * @date 2023-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysApp extends TenantModel implements Id<Long>, Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * appId
     */
    private String appId;

    /**
     * appSecret
     */
    private String appSecret;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 备注
     */
    private String remark;


}
