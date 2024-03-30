package cc.iotkit.system.dto.bo;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色信息业务对象 sys_role
 *
 * @author Michelle.Chung
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysRoleAuthBo extends BaseDto {


    @ApiModelProperty(value = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;


    @ApiModelProperty(value = "用户ID列表")
    private Long[] userIds;



}
