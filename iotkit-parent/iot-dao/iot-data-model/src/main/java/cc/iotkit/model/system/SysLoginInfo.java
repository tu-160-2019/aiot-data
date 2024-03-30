package cc.iotkit.model.system;

import cc.iotkit.model.Id;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;


/**
 * 系统访问记录视图对象 sys_logininfor
 *
 * @author Michelle.Chung
 * @date 2023-02-07
 */
@Data
public class SysLoginInfo implements Id<Long>,Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    private Long id;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录状态（0成功 1失败）
     */
    private String status;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;


    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private Date loginTime;


}
