package cc.iotkit.manager.dto.bo.plugin;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.model.plugin.PluginInfo;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件业务对象
 *
 * @author sjg
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PluginInfo.class, reverseConvertGenerate = false)
public class PluginInfoBo extends BaseDto {

    /**
     * id
     */
    @NotNull(message = "插件id不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 插件名称
     */
    @NotNull(message = "插件名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 部署方式
     */
    @NotNull(message = "部署方式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String deployType;

    /**
     * 插件类型
     */
    private String type;

    /**
     * 设备插件协议类型
     */
    private String protocol;

    /**
     * 状态
     */
    private String state;

    /**
     * 插件配置信息
     */
    private String config;

    /**
     * 插件脚本
     */
    private String script;

}
