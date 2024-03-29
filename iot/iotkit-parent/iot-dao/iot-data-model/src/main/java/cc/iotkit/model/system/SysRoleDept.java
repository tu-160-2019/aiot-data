package cc.iotkit.model.system;

import cc.iotkit.model.BaseModel;
import cc.iotkit.model.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * author: 石恒
 * date: 2023-05-30 16:16
 * description:
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleDept extends BaseModel implements Id<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long roleId;

    /**
     * 部门ID
     */
    private Long deptId;
}
