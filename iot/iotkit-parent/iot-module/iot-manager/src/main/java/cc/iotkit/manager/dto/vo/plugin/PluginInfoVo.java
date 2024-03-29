package cc.iotkit.manager.dto.vo.plugin;

import cc.iotkit.model.plugin.PluginInfo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.Date;

@Data
@AutoMapper(target = PluginInfo.class)
public class PluginInfoVo {

    /**
     * id
     */
    private Long id;

    /**
     * 插件包id
     */
    private String pluginId;

    /**
     * 插件名称
     */
    private String name;

    /**
     * 部署方式
     */
    private String deployType;

    /**
     * 插件包文件名
     */
    private String file;

    /**
     * 插件版本
     */
    private String version;

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
     * 描述
     */
    private String description;

    /**
     * 插件配置项描述
     */
    private String configSchema;

    /**
     * 插件配置信息
     */
    private String config;

    /**
     * 插件脚本
     */
    private String script;

    /**
     * 创建时间
     */
    private Date createTime;

}
