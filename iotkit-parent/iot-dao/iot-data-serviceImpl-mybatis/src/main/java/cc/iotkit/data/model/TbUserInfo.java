package cc.iotkit.data.model;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.listener.TenantListener;
import cc.iotkit.model.UserInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("user_info")
@AutoMapper(target = UserInfo.class)
@ApiModel(value = "用户信息表")
public class TbUserInfo extends BaseEntity implements TenantAware {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String uid;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;

    /**
     * 密钥（密码加密后的内容）
     */
    @ApiModelProperty(value = "密钥（密码加密后的内容）")
    private String secret;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 性别 0-未知 1-male,2-female
     */
    @ApiModelProperty(value="性别 0-未知 1-male,2-female")
    private Integer gender;

    /**
     * 头像地址
     */
    @ApiModelProperty(value="头像地址")
    private String avatarUrl;

    @ApiModelProperty(value="email")
    private String email;

    @ApiModelProperty(value="地址")
    private String address;

    /**
     * 当前家庭Id
     */
    @ApiModelProperty(value="当前家庭Id")
    private String currHomeId;

    /**
     * 用户类型
     * 0:平台用户
     * 1:终端用户
     */
    @ApiModelProperty(value="用户类型 0:平台用户 1:终端用户")
    private Integer type;

    /**
     * 角色
     */
    @ApiModelProperty(value="角色")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private String roles;

    /**
     * 权限
     */
    @ApiModelProperty(value="权限")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)

    private String permissions;

    /**
     * 用户使用的平台
     * 见:Constants.THIRD_PLATFORM
     */
    @ApiModelProperty(value="用户使用的平台")
    @ReverseAutoMapping(ignore = true)
    @AutoMapping(ignore = true)
    private String usePlatforms;

}
