package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.HomeMapper;
import cc.iotkit.data.model.TbHome;
import cc.iotkit.data.service.HomeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class HomeServiceImpl extends ServiceImpl<HomeMapper, TbHome> implements HomeService {
    @Override
    public TbHome findByUidAndCurrent(Long uid, boolean current) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbHome>().eq(TbHome::getUserId, uid).eq(TbHome::getCurrent, current));
    }

    @Override
    public TbHome checkHomeNameUnique(Long uid, String name) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbHome>()
                .eq(StringUtils.isNotBlank(uid.toString()), TbHome::getUserId, uid)
                .eq(StringUtils.isNotBlank(name), TbHome::getName, name));
    }

    @Override
    public List<TbHome> findByUid(Long uid) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbHome>().eq(StringUtils.isNotBlank(uid.toString()), TbHome::getUserId, uid));
    }
}
