package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysDictType;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SysDictTypeService extends IService<TbSysDictType> {

    List<TbSysDictType> findByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper);

    TbSysDictType findOneByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper);

    Page<TbSysDictType> findPageByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper, int page, int size);
}