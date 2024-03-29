package cc.iotkit.web.service;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.constant.GlobalConstants;
import cc.iotkit.common.enums.UserType;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.exception.user.UserException;
import cc.iotkit.common.redis.utils.RedisUtils;
import cc.iotkit.common.utils.MessageUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.web.config.properties.CaptchaProperties;
import cc.iotkit.system.dto.RegisterBody;
import cc.iotkit.system.dto.bo.SysUserBo;
import cc.iotkit.system.service.ISysUserService;
import cn.dev33.satoken.secure.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 注册校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysRegisterService {

    private final ISysUserService userService;
    private final CaptchaProperties captchaProperties;

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        String tenantId = registerBody.getTenantId();
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        // 校验用户类型是否存在
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        boolean captchaEnabled = captchaProperties.getEnable();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, registerBody.getCode(), registerBody.getUuid());
        }
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);

        if (!userService.checkUserNameUnique(sysUser)) {
            throw new UserException( username);
        }
        boolean regFlag = userService.registerUser(sysUser, tenantId);
        if (!regFlag) {
            throw new UserException( "注册失败" );
        }
        recordLoginInfo(tenantId, username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLoginInfo(tenantId, username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.expire"));
            throw new BizException("验证码过期");

        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLoginInfo(tenantId, username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.error"));
            throw new BizException("验证码错误");
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    private void recordLoginInfo(String tenantId, String username, String status, String message) {
//        LogininforEvent logininforEvent = new LogininforEvent();
//        logininforEvent.setTenantId(tenantId);
//        logininforEvent.setUsername(username);
//        logininforEvent.setStatus(status);
//        logininforEvent.setMessage(message);
//        logininforEvent.setRequest(ServletUtils.getRequest());
//        SpringUtils.context().publishEvent(logininforEvent);
    }

}
