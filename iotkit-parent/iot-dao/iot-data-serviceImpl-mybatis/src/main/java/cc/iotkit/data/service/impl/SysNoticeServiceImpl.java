package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysNoticeMapper;
import cc.iotkit.data.model.TbSysNotice;
import cc.iotkit.data.service.SysNoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("DBSysNoticeServiceImpl")
@Primary
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, TbSysNotice> implements SysNoticeService {

    @Override
    public Page<TbSysNotice> findByConditions(LambdaQueryWrapper<TbSysNotice> lambdaQueryWrapper, int page, int size) {
        Page<TbSysNotice> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }
}
