package cc.iotkit.data.model;

import cc.iotkit.model.screen.ScreenApi;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:02
 */
@Data
@Entity
@Table(name = "screen_api")
@AutoMapper(target = ScreenApi.class)
public class TbScreenApi {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private Long id;
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
    @Column(columnDefinition = "text")
    private String script;
}
