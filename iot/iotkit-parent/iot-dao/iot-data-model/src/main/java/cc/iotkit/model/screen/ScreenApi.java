package cc.iotkit.model.screen;

import cc.iotkit.model.Owned;
import lombok.Data;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:04
 */
@Data
public class ScreenApi implements Owned<Long> {

    private Long id;
    private String uid;
    /**
     * 大屏id
     */
    private Long screenId;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 接口参数
     */
    private String apiParams;

    /**
     * 请求方法
     */
    private String httpMethod;

    /**
     * 数据源
     */
    private String dataSource;

    /**
     * 创建时间
     */
    private Long createAt;

    /**
     * 转换脚本
     */
    private String script;
}
