package cc.iotkit.data.model;

import cc.iotkit.model.screen.Screen;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:02
 */
@Data
@TableName("screen")
@AutoMapper(target = Screen.class)
public class TbScreen {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
