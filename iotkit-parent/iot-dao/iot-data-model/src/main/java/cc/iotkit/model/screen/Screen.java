package cc.iotkit.model.screen;

import cc.iotkit.model.Owned;
import lombok.Data;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:04
 */
@Data
public class Screen implements Owned<Long> {

    public static final String STATE_STOPPED = "stopped";
    public static final String STATE_RUNNING = "running";

    private Long id;
    private String uid;
    /**
     * 大屏名称
     */
    private String name;

    /**
     * 资源文件
     */
    private String resourceFile;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 发布状态
     */
    private String state;

    /**
     * 创建时间
     */
    private Long createAt;

    /**
     * 是否为默认大屏
     */
    private Boolean isDefault;
}
