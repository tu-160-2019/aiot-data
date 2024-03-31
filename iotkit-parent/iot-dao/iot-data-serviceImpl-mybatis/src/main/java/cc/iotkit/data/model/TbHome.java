package cc.iotkit.data.model;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.listener.TenantListener;
import cc.iotkit.model.space.Home;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@TableName("home")
@ApiModel(value = "家庭信息")
@AutoMapper(target = Home.class)
public class TbHome extends BaseEntity implements TenantAware {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "家庭id")
    private Long id;

    /**
     * 家庭名称
     */
    @ApiModelProperty(value = "家庭名称")
    private String name;

    /**
     * 家庭地址
     */
    @ApiModelProperty(value = "家庭地址")
    private String address;

    /**
     * 关联用户id
     */
    @ApiModelProperty(value = "关联用户id")
    private Long userId;

    /**
     * 空间数量
     */
    @ApiModelProperty(value = "空间数量")
    private Integer spaceNum;

    /**
     * 设备数量
     */
    @ApiModelProperty(value = "设备数量")
    private Integer deviceNum;

    /**
     * 是否为用户当前使用的家庭
     */
    @ApiModelProperty(value = "是否为用户当前使用的家庭")
    private Boolean current;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;

}
