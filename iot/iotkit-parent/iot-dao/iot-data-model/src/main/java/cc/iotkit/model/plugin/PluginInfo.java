package cc.iotkit.model.plugin;

import cc.iotkit.model.BaseModel;
import cc.iotkit.model.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 插件信息
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PluginInfo extends BaseModel implements Id<Long>, Serializable {

    /**
     * 插件状态-停止
     */
    public static final String STATE_STOPPED = "stopped";
    /**
     * 插件状态-运行中
     */
    public static final String STATE_RUNNING = "running";

    /**
     * 插件类型-普通插件
     */
    public static final String TYPE_NORMAL = "normal";
    /**
     * 插件类型-设备插件
     */
    public static final String TYPE_DEVICE = "device";

    /**
     * 部署方式-上传jar
     */
    public static final String DEPLOY_UPLOAD = "upload";
    /**
     * 部署方式-独立运行
     */
    public static final String DEPLOY_ALONE = "alone";

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
     * 描述
     */
    private String description;

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
     * 插件配置项描述信息
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

}
