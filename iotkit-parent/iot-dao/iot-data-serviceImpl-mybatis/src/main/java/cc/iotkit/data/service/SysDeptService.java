package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysDept;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysDeptService extends IService<TbSysDept> {

    List<TbSysDept> findByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper);

    TbSysDept findOneByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper);

    Page<TbSysDept> findPageByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper, int page, int size);

    long countByParentId(Long parentId);

    List<TbSysDept> findByRoleId(Long roleId);
}
