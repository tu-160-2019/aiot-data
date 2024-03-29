package cc.iotkit.openapi.service.impl;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.enums.DeviceType;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.log.event.LogininforEvent;
import cc.iotkit.common.model.LoginUser;
import cc.iotkit.common.model.RoleDTO;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.CodecUtil;
import cc.iotkit.common.utils.DateUtils;
import cc.iotkit.common.utils.MessageUtils;
import cc.iotkit.common.utils.SpringUtils;
import cc.iotkit.common.web.utils.ServletUtils;
import cc.iotkit.data.system.ISysUserData;
import cc.iotkit.model.system.SysUser;
import cc.iotkit.openapi.dto.bo.TokenVerifyBo;
import cc.iotkit.openapi.service.OpenBaseService;
import cc.iotkit.system.service.ISysPermissionService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenBaseServiceImpl implements OpenBaseService {

    @Value("${openapi.appid}")
    private String appid;

    @Value("${openapi.password}")
    private String password;

    @Autowired
    private ISysUserData userData;

    @Autowired
    private ISysPermissionService permissionService;

    @Override
    public String getToken(TokenVerifyBo bo) {
        String boAppid = bo.getAppid();
        String boIdentifier = bo.getIdentifier();
        String boTimeStamp = bo.getTimestamp();

        // 校验租户
        checkTenant(bo.getTenantId());
        if (!CodecUtil.md5Str(boAppid + password + boTimeStamp).equals(boIdentifier)){
            throw new BizException(ErrCode.IDENTIFIER_ERROR);
        }
        if (!boAppid.equals(appid)){
            throw new BizException(ErrCode.API_LOGIN_ERROR);
        }
        SysUser sysUser = userData.selectUserByUserName(appid);
        if (sysUser == null){
            //用户不存在是否新建？
        }
        LoginUser loginUser = buildLoginUser(sysUser);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);

        recordLoginInfo(loginUser.getTenantId(), bo.getAppid(), Constants.LOGIN_SUCCESS, MessageUtils.message("openapi.login.success"));
        recordLoginInfo(sysUser.getId());
        return StpUtil.getTokenValue();
    }

    private void checkTenant(String tenantId) {

    }
    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setUserType(user.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user.getId()));
        loginUser.setRolePermission(permissionService.getRolePermission(user.getId()));
        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
        loginUser.setRoles(roles);
        return loginUser;
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    private void recordLoginInfo(String tenantId, String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }
    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = userData.findById(userId);
        sysUser.setLoginIp(ServletUtils.getClientIP());
        sysUser.setLoginDate(DateUtils.getNowDate());
        sysUser.setUpdateBy(userId);
        userData.save(sysUser);
    }
}
