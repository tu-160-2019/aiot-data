package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.TaskInfoMapper;
import cc.iotkit.data.model.TbTaskInfo;
import cc.iotkit.data.service.TaskInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TbTaskInfo> implements TaskInfoService {

    @Override
    public List<TbTaskInfo> findByUid(String uid) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbTaskInfo>()
                .eq(TbTaskInfo::getUid, uid).orderByDesc(TbTaskInfo::getCreateAt));
    }

    @Override
    public Page<TbTaskInfo> findPageByCondition(LambdaQueryWrapper<TbTaskInfo> wrapper, int page, int size) {
        Page<TbTaskInfo> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, wrapper);
    }
}
