package net.srt.security.service;

import lombok.AllArgsConstructor;
import net.srt.api.module.message.SmsApi;
import net.srt.framework.common.utils.Result;
import net.srt.framework.security.mobile.MobileVerifyCodeService;
import org.springframework.stereotype.Service;

/**
 * 短信验证码效验
 *
 * @author 阿沐 babamu@126.com
 */
@Service
@AllArgsConstructor
public class MobileVerifyCodeServiceImpl implements MobileVerifyCodeService {
    private final SmsApi smsApi;

    @Override
    public boolean verifyCode(String mobile, String code) {
        Result<Boolean> result = smsApi.verifyCode(mobile, code);

        return result.getData();
    }
}
