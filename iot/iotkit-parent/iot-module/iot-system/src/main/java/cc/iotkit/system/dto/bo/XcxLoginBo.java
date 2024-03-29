package cc.iotkit.system.dto.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 小程序登录对象
 *
 * @author tfd
 */

@Data
public class XcxLoginBo {

    /**
     * appId
     */
    @NotBlank(message = "appId不能为空")
    private String appId;

    /**
     * 授权码
     */
    @NotBlank(message = "code不能为空")
    private String code;

}
