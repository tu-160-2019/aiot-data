package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.UserInfoMapper;
import cc.iotkit.data.model.TbUserInfo;
import cc.iotkit.data.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, TbUserInfo> implements UserInfoService {

    @Override
    public TbUserInfo findByUid(String uid) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbUserInfo>()
                .eq(TbUserInfo::getUid, uid));

    }

    @Override
    public List<TbUserInfo> findByType(int type) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbUserInfo>()
                .eq(TbUserInfo::getType, type));
    }

    @Override
    public List<TbUserInfo> findByTypeAndOwnerId(int type, String ownerId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbUserInfo>()
                .eq(TbUserInfo::getType, type)
                .eq(TbUserInfo::getTenantId, ownerId));
    }
}
