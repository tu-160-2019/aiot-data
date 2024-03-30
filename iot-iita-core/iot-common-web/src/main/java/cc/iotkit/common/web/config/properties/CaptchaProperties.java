package cc.iotkit.common.web.config.properties;

import cc.iotkit.common.web.enums.CaptchaCategory;
import cc.iotkit.common.web.enums.CaptchaType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码 配置属性
 *
 * @author Lion Li
 */
@Data
@ConfigurationProperties(prefix = "captcha")
@Component
public class CaptchaProperties {

    private Boolean enable;

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证码类别
     */
    private CaptchaCategory category;

    /**
     * 数字验证码位数
     */
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;
}
