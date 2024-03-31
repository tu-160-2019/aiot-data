package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysConfigService extends IService<TbSysConfig> {

    TbSysConfig findByConfigKey(String configKey);

    Page<TbSysConfig> findAll(LambdaQueryWrapper<TbSysConfig> wrapper, int page, int size);

    List<TbSysConfig> findAllByCondition(LambdaQueryWrapper<TbSysConfig> wrapper);
    TbSysConfig findOneByCondition(LambdaQueryWrapper<TbSysConfig> wrapper);
}
