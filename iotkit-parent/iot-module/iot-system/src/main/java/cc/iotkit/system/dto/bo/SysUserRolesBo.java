package cc.iotkit.system.dto.bo;

import cc.iotkit.common.api.BaseDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户权限设置
 *
 * @author sjg
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysUserRolesBo extends BaseDto {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色组
     */
    @Size(min = 1, message = "用户角色不能为空")
    private List<Long> roleIds;

}
