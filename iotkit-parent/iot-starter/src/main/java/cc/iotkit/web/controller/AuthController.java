package cc.iotkit.web.controller;

import cc.iotkit.common.api.Request;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.system.dto.LoginBody;
import cc.iotkit.system.dto.RegisterBody;
import cc.iotkit.system.dto.bo.SysTenantBo;
import cc.iotkit.system.dto.bo.XcxLoginBo;
import cc.iotkit.system.dto.vo.SysTenantVo;
import cc.iotkit.system.service.ISysConfigService;
import cc.iotkit.system.service.ISysTenantService;
import cc.iotkit.web.domain.vo.LoginTenantVo;
import cc.iotkit.web.domain.vo.LoginVo;
import cc.iotkit.web.domain.vo.TenantListVo;
import cc.iotkit.web.service.SysLoginService;
import cc.iotkit.web.service.SysRegisterService;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证
 *
 * @author Lion Li
 */
@Api(tags = "认证")
@SaIgnore
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysLoginService loginService;
    private final SysRegisterService registerService;
    private final ISysConfigService configService;
    private final ISysTenantService tenantService;

    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public LoginVo login(@Validated @RequestBody Request<LoginBody> body) {
        LoginBody loginBody = body.getData();
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        String token = loginService.login(
                loginBody.getTenantId(),
                loginBody.getUsername(), loginBody.getPassword(),
                loginBody.getCode(), loginBody.getUuid());
        loginVo.setToken(token);
        return loginVo;
    }

    /**
     * 小程序登录(示例)
     *
     * @param body 小程序appid,code
     * @return 结果
     */
    @ApiOperation("小程序登录")
    @PostMapping("/xcxLogin")
    public LoginVo xcxLogin(@Validated @RequestBody Request<XcxLoginBo> body) {
        LoginVo loginVo = new LoginVo();
        XcxLoginBo data = body.getData();
        // 生成令牌
        String token = loginService.xcxLogin(data.getAppId(), data.getCode());
        loginVo.setToken(token);
        return loginVo;
    }


    /**
     * 退出登录
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public void logout() {
        loginService.logout();

    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public void register(@Validated @RequestBody RegisterBody user) {
        if (!configService.selectRegisterEnabled(user.getTenantId())) {
            throw new BizException("当前租户不允许注册");
        }
        registerService.register(user);
    }

    /**
     * 登录页面租户下拉框
     *
     * @return 租户列表
     */
    @ApiOperation("登录页面租户下拉框")
    @PostMapping("/tenant/list")
    public LoginTenantVo tenantList(HttpServletRequest request) throws Exception {
        List<SysTenantVo> tenantList = tenantService.queryList(new SysTenantBo());
        List<TenantListVo> voList = tenantList.stream().map(t -> TenantListVo.builder()
                .tenantId(t.getTenantId())
                .companyName(t.getCompanyName())
                .domain(t.getDomain())
                .build()).collect(Collectors.toList());
        // 获取域名
        String host = new URL(request.getRequestURL().toString()).getHost();
        // 根据域名进行筛选
        List<TenantListVo> list = StreamUtils.filter(voList, vo -> StringUtils.equals(vo.getDomain(), host));
        // 返回对象
        LoginTenantVo vo = new LoginTenantVo();
        vo.setVoList(CollUtil.isNotEmpty(list) ? list : voList);
        vo.setTenantEnabled(true);
        return vo;
    }

}
