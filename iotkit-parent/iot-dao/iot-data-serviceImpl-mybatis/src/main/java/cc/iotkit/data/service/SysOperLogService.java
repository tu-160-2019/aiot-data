package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysOperLog;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SysOperLogService extends IService<TbSysOperLog> {

    List<TbSysOperLog> findByCondition(LambdaQueryWrapper<TbSysOperLog> wrapper);

    Page<TbSysOperLog> findPageByCondition (LambdaQueryWrapper<TbSysOperLog> wrapper, int page, int size);

    void deleteAll();
}
