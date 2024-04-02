package cc.iotkit.data.mapper;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.Id;
import cc.iotkit.model.TenantModel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.io.Serializable;

/**
 * @Author: jay
 * @Date: 2023/6/1 9:27
 * @Version: V1.0
 * @Description: 基础数据操作接口
 */
public interface IJPACommData<T extends Id<ID>, ID> extends ICommonData<T, ID> {
//public interface IJPACommData<T extends Id<ID>, ID extends Serializable, E> extends ICommonData<T, ID> {


    IService getBaseRepository();

    Class getJpaRepositoryClass();

    Class getTClass();

    @Override
    default T findById(ID id) {
        return (T) MapstructUtils.convert(Optional.of(getBaseRepository().getById((Serializable) id)).orElse(null), getTClass());
    }

    @Override
    default List<T> findByIds(Collection<ID> id) {
        List allById = getBaseRepository().listByIds(id);
        return MapstructUtils.convert(allById, getTClass());
    }

    @Override
    default T save(T data) {
        ID id = data.getId();
        Object tbData = MapstructUtils.convert(data, getJpaRepositoryClass());
        Optional byId = id == null ? Optional.empty() : Optional.of(getBaseRepository().getById((Serializable) id));
        if (byId.isPresent()) {
            Object dbObj = byId.get();
            //只更新不为空的字段
            BeanUtil.copyProperties(tbData, dbObj, CopyOptions.create().ignoreNullValue());
            tbData = dbObj;
        }

        if (tbData instanceof TenantAware) {
            String sourceTid = null;
            if (data instanceof TenantModel) {
                sourceTid = ((TenantModel) data).getTenantId();
            }
            String tenantId = TenantHelper.getTenantId();
            //未指定租户id,使用当前用户所属租户id
            if (StringUtils.isBlank(sourceTid) && tenantId != null) {
                ((TenantAware) tbData).setTenantId(tenantId);
            }
        }

//        getBaseRepository().saveOrUpdate(MapstructUtils.convert(tbData, getJpaRepositoryClass()));
        getBaseRepository().saveOrUpdate(tbData);
        return (T) MapstructUtils.convert(tbData, getTClass());
    }

    @Override
    default void batchSave(List<T> data) {
        getBaseRepository().saveBatch(MapstructUtils.convert(data, getJpaRepositoryClass()));
    }

    @Override
    default void deleteById(ID id) {
        getBaseRepository().removeById(id);
    }

    @Override
    default void deleteByIds(Collection<ID> ids) {
        getBaseRepository().removeBatchByIds(ids);
    }

    @Override
    default long count() {
        return getBaseRepository().count(new LambdaQueryWrapper<>());
    }

    @Override
    default List<T> findAll() {
        return MapstructUtils.convert(getBaseRepository().list(new LambdaQueryWrapper()), getTClass());
    }

    @Override
    default Paging<T> findAll(PageRequest<T> pageRequest) {
//        Page<E> rowPage = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
//        Page<E> paged = (Page<E>) getBaseRepository().getBaseMapper().selectPage(rowPage, new LambdaQueryWrapper<E>());
//        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), getTClass()));

        Page rowPage = new Page(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page paged = (Page) getBaseRepository().getBaseMapper().selectPage(rowPage, new LambdaQueryWrapper<>());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), getTClass()));
    }

    /**
     * 按条件查询多个结果
     */
    @Override
    default List<T> findAllByCondition(T data) {
//        List all = getBaseRepository().list(new LambdaQueryWrapper<>());
        Object convert =  MapstructUtils.convert(data, getJpaRepositoryClass());
        List all = getBaseRepository().list(new LambdaQueryWrapper<>(convert));
        return MapstructUtils.convert(all, getTClass());
    }

    /**
     * 按条件查询单个结果
     */
    @Override
    default T findOneByCondition(T data) {
//        E convert = (E) MapstructUtils.convert(data, getJpaRepositoryClass());
        Object convert =  MapstructUtils.convert(data, getJpaRepositoryClass());
        Object one = getBaseRepository().getOne(new LambdaQueryWrapper<>(convert));
        if (Objects.isNull(one)) {
            return null;
        }
        return (T) MapstructUtils.convert(one, getTClass());
    }

}
