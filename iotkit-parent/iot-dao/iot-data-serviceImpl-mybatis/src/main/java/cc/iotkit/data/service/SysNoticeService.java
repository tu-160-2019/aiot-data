package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysNotice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysNoticeService extends IService<TbSysNotice> {
    Page<TbSysNotice> findByConditions(LambdaQueryWrapper<TbSysNotice> lambdaQueryWrapper, int page, int size);
}
