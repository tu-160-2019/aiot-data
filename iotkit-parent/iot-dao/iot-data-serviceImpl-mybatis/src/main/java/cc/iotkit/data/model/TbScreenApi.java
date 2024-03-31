package cc.iotkit.data.model;

import cc.iotkit.model.screen.ScreenApi;
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
@TableName("screen_api")
@AutoMapper(target = ScreenApi.class)
public class TbScreenApi {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
    private String script;
}
