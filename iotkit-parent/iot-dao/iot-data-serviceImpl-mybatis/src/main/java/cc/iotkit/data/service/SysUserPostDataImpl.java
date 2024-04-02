package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysUserPost;
import cc.iotkit.data.system.ISysUserPostData;
import cc.iotkit.model.system.SysUserPost;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/5/30 17:04
 */

@Primary
@Service
@RequiredArgsConstructor
public class SysUserPostDataImpl implements ISysUserPostData, IJPACommData<SysUserPost, Long> {
//public class SysUserPostDataImpl implements ISysUserPostData, IJPACommData<SysUserPost, Long, TbSysUserPost> {

    private final SysUserPostService sysUserPostService;

    @Override
    public int deleteByUserId(Long userId) {
        boolean res = sysUserPostService.remove(new LambdaQueryWrapper<TbSysUserPost>()
                .eq(TbSysUserPost::getUserId, userId));
        return BooleanUtils.toInteger(res);
    }

    @Override
    public SysUserPostService getBaseRepository() {
        return sysUserPostService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysUserPost.class;
    }

    @Override
    public Class getTClass() {
        return SysUserPost.class;
    }

    @Override
    public void batchSave(List<SysUserPost> data) {
        sysUserPostService.saveBatch(MapstructUtils.convert(data, TbSysUserPost.class));
    }
}
