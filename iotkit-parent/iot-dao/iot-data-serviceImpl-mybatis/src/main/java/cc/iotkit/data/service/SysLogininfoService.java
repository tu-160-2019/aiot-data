package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysLogininfor;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SysLogininfoService extends IService<TbSysLogininfor> {
    List<TbSysLogininfor> findByConditions(LambdaQueryWrapper<TbSysLogininfor> wrapper);

    Page<TbSysLogininfor> findByConditions(LambdaQueryWrapper<TbSysLogininfor> wrapper, int page, int size);

    void deleteAll();
}
