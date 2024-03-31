package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysLogininforMapper;
import cc.iotkit.data.model.TbSysLogininfor;
import cc.iotkit.data.service.SysLogininfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class SysLoginInfoServiceImpl extends ServiceImpl<SysLogininforMapper, TbSysLogininfor> implements SysLogininfoService {

    @Override
    public List<TbSysLogininfor> findByConditions(LambdaQueryWrapper<TbSysLogininfor> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Page<TbSysLogininfor> findByConditions(LambdaQueryWrapper<TbSysLogininfor> wrapper, int page, int size) {
        Page<TbSysLogininfor> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, wrapper);
    }

    @Override
    public void deleteAll() {
        this.baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
