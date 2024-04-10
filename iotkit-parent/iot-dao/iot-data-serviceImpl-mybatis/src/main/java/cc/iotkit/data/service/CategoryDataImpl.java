/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.model.TbCategory;
import cc.iotkit.model.product.Category;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
public class CategoryDataImpl implements ICategoryData, IJPACommData<Category, String> {
//public class CategoryDataImpl implements ICategoryData, IJPACommData<Category, String, TbCategory> {

    @Autowired
    private CategoryService categoryService;

    @Override
    public CategoryService getBaseRepository() {
        return categoryService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbCategory.class;
    }

    @Override
    public Class getTClass() {
        return Category.class;
    }

    @Override
    public Category findById(String s) {
        return MapstructUtils.convert(categoryService.getById(s), Category.class);
    }

    @Override
    public List<Category> findByIds(Collection<String> id) {
        return Collections.emptyList();
    }

    @Override
    public Category save(Category data) {
        categoryService.saveOrUpdate(MapstructUtils.convert(data, TbCategory.class));

        TbCategory td = categoryService.getOne(new LambdaQueryWrapper<TbCategory>()
                .eq(TbCategory::getName, data.getName()));

        return MapstructUtils.convert(td, Category.class);
    }

    @Override
    public long count() {
        return categoryService.count();
    }

    @Override
    public List<Category> findAll() {
        return categoryService.list().stream()
                .map(c -> MapstructUtils.convert(c, Category.class))
                .collect(Collectors.toList());
    }

}
