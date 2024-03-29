package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysPost;

import java.util.List;

/**
 * 操作日志数据接口
 *
 * @author sjg
 */
public interface ISysPostData extends ICommonData<SysPost, Long> {
    List<Long> selectPostListByUserId(Long userId);

    List<SysPost> selectPostList(SysPost post);

    boolean checkPostNameUnique(SysPost post);

    boolean checkPostCodeUnique(SysPost post);
}
