package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysDictData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysDictDataService extends IService<TbSysDictData> {

    List<TbSysDictData> findByCondition(LambdaQueryWrapper<TbSysDictData> wrapper);

    Page<TbSysDictData> findPageByCondition (LambdaQueryWrapper<TbSysDictData> wrapper, int page, int size);
}
