package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysPostMapper;
import cc.iotkit.data.model.TbSysPost;
import cc.iotkit.data.service.SysPostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSysPostServiceImpl")
@Primary
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, TbSysPost> implements SysPostService {

    @Override
    public List<TbSysPost> findByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysPost findOneByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysPost> findPageByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper, int page, int size) {
        Page<TbSysPost> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return this.baseMapper.selectPostListByUserId(userId);
    }

}
