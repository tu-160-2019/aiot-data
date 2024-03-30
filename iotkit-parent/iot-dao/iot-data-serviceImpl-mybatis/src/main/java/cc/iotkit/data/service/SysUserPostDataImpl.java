package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysUserPostRepository;
import cc.iotkit.data.model.TbSysUserPost;
import cc.iotkit.data.system.ISysUserPostData;
import cc.iotkit.model.system.SysUserPost;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
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


    private final SysUserPostRepository sysUserPostRepository;

    @Override
    public int deleteByUserId(Long userId) {
        return sysUserPostRepository.deleteAllByUserId(userId);
    }

    @Override
    public JpaRepository getBaseRepository() {
        return sysUserPostRepository;
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
        sysUserPostRepository.saveAll(MapstructUtils.convert(data, TbSysUserPost.class));
    }
}
