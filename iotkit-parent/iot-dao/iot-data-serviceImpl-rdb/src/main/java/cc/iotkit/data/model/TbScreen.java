package cc.iotkit.data.model;

import cc.iotkit.model.screen.Screen;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:02
 */
@Data
@Entity
@Table(name = "screen")
@AutoMapper(target = Screen.class)
public class TbScreen {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private Long id;
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
