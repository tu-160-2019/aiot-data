package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysOperLogMapper;
import cc.iotkit.data.model.TbSysOperLog;
import cc.iotkit.data.service.SysOperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSysOperLogServiceImpl")
@Primary
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, TbSysOperLog> implements SysOperLogService {

    @Override
    public List<TbSysOperLog> findByCondition(LambdaQueryWrapper<TbSysOperLog> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Page<TbSysOperLog> findPageByCondition(LambdaQueryWrapper<TbSysOperLog> wrapper, int page, int size) {
        Page<TbSysOperLog> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, wrapper);
    }

    @Override
    public void deleteAll() {
        this.baseMapper.delete(new LambdaQueryWrapper<>());
    }

}
