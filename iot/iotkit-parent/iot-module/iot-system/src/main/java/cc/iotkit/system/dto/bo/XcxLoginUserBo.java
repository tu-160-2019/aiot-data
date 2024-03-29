package cc.iotkit.system.dto.bo;

import cc.iotkit.common.model.LoginUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 小程序登录用户身份权限
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class XcxLoginUserBo extends LoginUser {


    private static final long serialVersionUID = 1L;

    /**
     * openid
     */
    private String openid;

}
