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
import cc.iotkit.data.dao.CategoryRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.model.TbCategory;
import cc.iotkit.model.product.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
public class CategoryDataImpl implements ICategoryData, IJPACommData<Category, String> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return categoryRepository;
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
        return MapstructUtils.convert(categoryRepository.findById(s).orElse(null), Category.class);
    }

    @Override
    public List<Category> findByIds(Collection<String> id) {
        return Collections.emptyList();
    }

    @Override
    public Category save(Category data) {
        TbCategory tb = categoryRepository.save(MapstructUtils.convert(data, TbCategory.class));
        data.setId(tb.getId());
        return data;
    }





    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll().stream()
                .map(c -> MapstructUtils.convert(c, Category.class))
                .collect(Collectors.toList());
    }



}
