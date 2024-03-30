package cc.iotkit.model;

import cc.iotkit.common.utils.MapstructUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 *
 * @author Lion Li
 */

@Data
public class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    public <T> T to(Class<T> tClass) {
        return MapstructUtils.convert(this, tClass);
    }
}
