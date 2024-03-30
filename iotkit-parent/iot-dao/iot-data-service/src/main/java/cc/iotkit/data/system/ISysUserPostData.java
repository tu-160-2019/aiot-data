package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysUserPost;

/**
 * 用户岗位数据接口
 *
 * @author sjg
 */
public interface ISysUserPostData extends ICommonData<SysUserPost, Long> {

    /**
     * 按用户id删除数据
     *
     * @param userId 用户id
     * @return 数量
     */
    int deleteByUserId(Long userId);
}
