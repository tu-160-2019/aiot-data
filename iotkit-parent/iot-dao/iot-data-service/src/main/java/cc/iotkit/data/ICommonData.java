/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.Id;

import java.util.Collection;
import java.util.List;

/**
 * 数据基础服务接口
 */
public interface ICommonData<T extends Id<ID>, ID> {

    /**
     * 通过ID取数据
     */
     T findById(ID id);

    /**
     * 通过ID取数据
     */
     List<T> findByIds(Collection<ID> id);


    /**
     * 保存数据，id不为空更新，否则添加
     */
     T save(T data) ;


    /**
     * 批量保存数据
     */
     void batchSave(List<T> data) ;
    /**
     * 按id删除
     */
     void deleteById(ID id) ;
    /**
     * 按id批量删除
     */
     void deleteByIds(Collection<ID> ids) ;
    /**
     * 总数统计
     */
     long count();
    /**
     * 取所有数据
     */
     List<T> findAll() ;

    /**
     * 分页获取所有信息
     */
     Paging<T> findAll(PageRequest<T> pageRequest) ;

    /**
     * 按条件查询多个结果
     */
     List<T> findAllByCondition(T data);

    /**
     * 按条件查询单个结果
     */
     T findOneByCondition(T data);
}
