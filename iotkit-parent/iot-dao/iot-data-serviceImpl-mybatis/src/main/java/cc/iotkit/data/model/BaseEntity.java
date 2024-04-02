package cc.iotkit.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.io.Serializable;

/**
 * Entity基类
 *
 * @author Lion Li
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 创建部门
     */
    @ApiModelProperty(value = "创建部门")
    private Long createDept;

    /**
     * 创建者
     */
    @CreatedBy
    @TableField("create_by")
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 创建时间
     */
    @CreatedDate
    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @LastModifiedBy
    @TableField("update_by")
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @TableField("update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
