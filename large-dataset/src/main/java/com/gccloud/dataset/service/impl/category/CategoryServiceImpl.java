/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.dataset.service.impl.category;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.service.ITreeService;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.dataset.dao.CategoryDao;
import com.gccloud.dataset.dto.CategorySearchDTO;
import com.gccloud.dataset.entity.CategoryEntity;
import com.gccloud.dataset.service.ICategoryService;
import com.gccloud.dataset.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 13:50
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements ICategoryService {

    /**
     * 顶级父节点id
     */
    private static final String SUPER_PARENT_ID = "0";

    /**
     * id序列分隔符
     */
    private static final String ID_SPLIT = ",";


    @Resource
    private ITreeService treeService;

    @Override
    public List<CategoryVO> getTree(CategorySearchDTO searchDTO) {
        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(searchDTO.getType()), CategoryEntity::getType, searchDTO.getType());
        List<CategoryEntity> list = this.list(wrapper);
        List<CategoryVO> voList = BeanConvertUtils.convert(list, CategoryVO.class);
        treeService.transToTree(voList);
        voList.removeIf(categoryVO -> !SUPER_PARENT_ID.equals(categoryVO.getParentId()));
        return voList;
    }

    @Override
    public List<String> getAllChildrenId(String id) {
        CategoryEntity category = this.getById(id);
        String ids = category.getIds() + ID_SPLIT;
        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(CategoryEntity::getId);
        wrapper.likeRight(CategoryEntity::getIds, ids);
        List<CategoryEntity> list = this.list(wrapper);
        return list.stream().map(CategoryEntity::getId).collect(Collectors.toList());
    }


    @Override
    public String add(CategoryEntity entity) {
        boolean repeat = this.checkNameRepeat(entity);
        if (repeat) {
            throw new GlobalException("节点名称重复");
        }
        if (StringUtils.isBlank(entity.getParentId())) {
            entity.setParentId(SUPER_PARENT_ID);
        }
        this.save(entity);
        entity.setIds(entity.getId());
        if (!SUPER_PARENT_ID.equals(entity.getParentId())) {
            CategoryEntity parent = this.getById(entity.getParentId());
            if (parent == null) {
                throw new GlobalException("父节点不存在");
            }
            entity.setIds(parent.getIds() + ID_SPLIT + entity.getId());
        }
        this.updateById(entity);
        return entity.getId();
    }

    @Override
    public void update(CategoryEntity entity) {
        boolean repeat = this.checkNameRepeat(entity);
        if (repeat) {
            throw new GlobalException("节点名称重复");
        }
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CategoryEntity::getId);
        queryWrapper.eq(CategoryEntity::getParentId, id);
        if (this.list(queryWrapper).size() > 0) {
            throw new GlobalException("该节点下存在子节点，无法删除");
        }
        this.removeById(id);
    }

    @Override
    public boolean checkNameRepeat(CategoryEntity entity) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CategoryEntity::getId);
        queryWrapper.eq(CategoryEntity::getName, entity.getName());
        queryWrapper.eq(StringUtils.isNotBlank(entity.getType()), CategoryEntity::getType, entity.getType());
        queryWrapper.ne(StringUtils.isNotBlank(entity.getId()), CategoryEntity::getId, entity.getId());
        if (StringUtils.isBlank(entity.getParentId())) {
            queryWrapper.eq(CategoryEntity::getParentId, SUPER_PARENT_ID);
        } else {
            queryWrapper.eq(CategoryEntity::getParentId, entity.getParentId());
        }
        // NOTE 为了兼容mybatis-plus的升级，这里不能使用count()方法
        return this.list(queryWrapper).size() > 0;
    }
}