package cc.iotkit.model.system;

import cc.iotkit.model.BaseModel;
import cc.iotkit.model.Id;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户和角色关联 sys_user_role
 *
 * @author Michelle.Chung
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserRole extends BaseModel implements Id<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}
