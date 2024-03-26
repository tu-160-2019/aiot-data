package net.srt.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import net.srt.api.module.message.SmsApi;
import net.srt.framework.common.constant.Constant;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.framework.security.mobile.MobileAuthenticationToken;
import net.srt.framework.security.user.UserDetail;
import net.srt.framework.security.utils.TokenUtils;
import net.srt.system.enums.LoginOperationEnum;
import net.srt.system.enums.SuperAdminEnum;
import net.srt.system.service.SysAuthService;
import net.srt.system.service.SysCaptchaService;
import net.srt.system.service.SysLogLoginService;
import net.srt.system.service.SysUserService;
import net.srt.system.vo.SysAccountLoginVO;
import net.srt.system.vo.SysMobileLoginVO;
import net.srt.system.vo.SysTokenVO;
import net.srt.system.vo.SysUserVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 权限认证服务
 *
 * @author 阿沐 babamu@126.com
 */
@Service
@AllArgsConstructor
public class SysAuthServiceImpl implements SysAuthService {
	private final SysCaptchaService sysCaptchaService;
	private final TokenStoreCache tokenStoreCache;
	private final AuthenticationManager authenticationManager;
	private final SysLogLoginService sysLogLoginService;
	private final SysUserService sysUserService;
	private final SmsApi smsApi;

	@Override
	public SysTokenVO loginByAccount(SysAccountLoginVO login) {
		// 验证码效验
		boolean flag = sysCaptchaService.validate(login.getKey(), login.getCaptcha());
		if (!flag) {
			// 保存登录日志
			sysLogLoginService.save(login.getUsername(), Constant.FAIL, LoginOperationEnum.CAPTCHA_FAIL.getValue());

			throw new ServerException("验证码错误");
		}

		Authentication authentication;
		try {
			// 用户认证
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
		} catch (BadCredentialsException e) {
			throw new ServerException("用户名或密码错误");
		}

		// 用户信息
		UserDetail user = (UserDetail) authentication.getPrincipal();

		//判断是否有项目id列表，没有，禁止登录
		if (!SuperAdminEnum.YES.getValue().equals(user.getSuperAdmin()) && CollectionUtils.isEmpty(user.getProjectIds())) {
			throw new ServerException("您没有项目租户空间可用，请联系管理员分配项目空间！");
		}

		// 生成 accessToken
		String accessToken = TokenUtils.generator();

		// 保存用户信息到缓存
		tokenStoreCache.saveUser(accessToken, user);

		return new SysTokenVO(accessToken);
	}

	@Override
	public SysTokenVO loginByMobile(SysMobileLoginVO login) {
		Authentication authentication;
		try {
			// 用户认证
			authentication = authenticationManager.authenticate(
					new MobileAuthenticationToken(login.getMobile(), login.getCode()));
		} catch (BadCredentialsException e) {
			throw new ServerException("手机号或验证码错误");
		}

		// 用户信息
		UserDetail user = (UserDetail) authentication.getPrincipal();

		// 生成 accessToken
		String accessToken = TokenUtils.generator();

		// 保存用户信息到缓存
		tokenStoreCache.saveUser(accessToken, user);

		return new SysTokenVO(accessToken);
	}

	@Override
	public void sendCode(String mobile) {
		// 生成6位验证码
		String code = RandomUtil.randomNumbers(6);

		SysUserVO user = sysUserService.getByMobile(mobile);
		if (user == null) {
			throw new ServerException("手机号未注册");
		}

		// 发送短信
		smsApi.sendCode(mobile, "code", code);
	}

	@Override
	public void logout(String accessToken) {
		// 用户信息
		UserDetail user = tokenStoreCache.getUser(accessToken);

		// 删除用户信息
		tokenStoreCache.deleteUser(accessToken);

		// 保存登录日志
		sysLogLoginService.save(user.getUsername(), Constant.SUCCESS, LoginOperationEnum.LOGOUT_SUCCESS.getValue());
	}
}
