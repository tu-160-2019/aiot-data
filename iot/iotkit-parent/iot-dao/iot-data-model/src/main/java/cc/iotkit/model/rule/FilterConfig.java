package cc.iotkit.model.rule;

import lombok.Data;

/**
 * 监听器和过滤器的过滤条件配置
 *
 * @author sjg
 */
@Data
public class FilterConfig {
    /**
     * 条件类型
     */
    private String type;

    /**
     * 条件配置
     */
    protected String config;
}
