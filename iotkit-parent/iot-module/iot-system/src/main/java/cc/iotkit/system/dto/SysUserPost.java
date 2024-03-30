package cc.iotkit.system.dto;

import lombok.Data;

/**
 * 用户和岗位关联
 *
 * @author Lion Li
 */

@Data
public class SysUserPost {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;

}
