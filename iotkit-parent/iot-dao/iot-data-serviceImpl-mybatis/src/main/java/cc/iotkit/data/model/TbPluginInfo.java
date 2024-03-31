package cc.iotkit.data.model;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.listener.TenantListener;
import cc.iotkit.model.plugin.PluginInfo;
import cc.iotkit.model.product.Product;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author sjg
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "插件信息")
@TableName("plugin_info")
@AutoMapper(target = PluginInfo.class)
public class TbPluginInfo extends BaseEntity implements TenantAware {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 插件包id
     */
    @ApiModelProperty(value = "插件包id")
    private String pluginId;

    /**
     * 插件名称
     */
    @ApiModelProperty(value = "插件名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 部署方式
     */
    @ApiModelProperty(value = "部署方式")
    private String deployType;

    /**
     * 插件包地址
     */
    @ApiModelProperty(value = "插件包地址")
    private String file;

    /**
     * 插件版本
     */
    @ApiModelProperty(value = "插件版本")
    private String version;

    /**
     * 插件类型
     */
    @ApiModelProperty(value = "插件类型")
    private String type;

    /**
     * 设备插件协议类型
     */
    @ApiModelProperty(value = "设备插件协议类型")
    private String protocol;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String state;

    /**
     * 插件配置项描述信息
     */
    @ApiModelProperty(value = "插件配置项描述信息")
    private String configSchema;

    /**
     * 插件配置信息
     */
    @ApiModelProperty(value = "插件配置信息")
    private String config;

    /**
     * 插件脚本
     */
    @ApiModelProperty(value = "插件脚本")
    private String script;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;
}
