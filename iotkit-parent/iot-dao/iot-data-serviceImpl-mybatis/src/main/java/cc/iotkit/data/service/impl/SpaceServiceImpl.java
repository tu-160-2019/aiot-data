package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SpaceMapper;
import cc.iotkit.data.model.TbSpace;
import cc.iotkit.data.service.SpaceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSpaceServiceImpl")
@Primary
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, TbSpace> implements SpaceService {

    @Override
    public List<TbSpace> findByUidOrderByCreateAtDesc(String uid) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbSpace>()
                .eq(TbSpace::getTenantId, uid).orderByDesc(TbSpace::getCreateTime));
    }

    @Override
    public List<TbSpace> findByUidAndHomeIdOrderByCreateAtDesc(String uid, String homeId) {
       return this.baseMapper.selectList(new LambdaQueryWrapper<TbSpace>()
                .eq(TbSpace::getTenantId, uid)
                .eq(TbSpace::getHomeId, homeId)
                .orderByDesc(TbSpace::getCreateTime));
    }

    @Override
    public List<TbSpace> findByHomeId(Long homeId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbSpace>()
                .eq(TbSpace::getHomeId, homeId));

    }

    @Override
    public List<TbSpace> findByUid(String uid) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbSpace>()
                .eq(TbSpace::getTenantId, uid));
    }

    @Override
    public Page<TbSpace> findByUid(String uid, int page, int size) {
        Page<TbSpace> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbSpace>().eq(TbSpace::getTenantId, uid));
    }
}
