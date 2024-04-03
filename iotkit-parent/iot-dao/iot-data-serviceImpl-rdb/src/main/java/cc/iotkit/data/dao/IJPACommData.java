package cc.iotkit.data.dao;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.ICommonData;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.model.Id;
import cc.iotkit.model.TenantModel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: jay
 * @Date: 2023/6/1 9:27
 * @Version: V1.0
 * @Description: 基础数据操作接口
 */
public interface IJPACommData<T extends Id<ID>, ID> extends ICommonData<T, ID> {


    JpaRepository getBaseRepository();

    Class getJpaRepositoryClass();

    Class getTClass();

    @Override
    default T findById(ID id) {
        return (T) MapstructUtils.convert(getBaseRepository().findById(id).orElse(null), getTClass());
    }

    @Override
    default List<T> findByIds(Collection<ID> id) {
        List allById = getBaseRepository().findAllById(id);
        return MapstructUtils.convert(allById, getTClass());
    }

    @Override
    default T save(T data) {
        ID id = data.getId();
        Object tbData = MapstructUtils.convert(data, getJpaRepositoryClass());
        Optional byId = id == null ? Optional.empty() : getBaseRepository().findById(id);
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

        Object o = getBaseRepository().save(tbData);
        return (T) MapstructUtils.convert(o, getTClass());
    }

    @Override
    default void batchSave(List<T> data) {
        getBaseRepository().saveAll(MapstructUtils.convert(data, getJpaRepositoryClass()));
    }

    @Override
    default void deleteById(ID id) {
        getBaseRepository().deleteById(id);
    }

    @Override
    default void deleteByIds(Collection<ID> ids) {
        getBaseRepository().deleteAllById(ids);
    }

    @Override
    default long count() {
        return getBaseRepository().count();
    }

    @Override
    default List<T> findAll() {
        return MapstructUtils.convert(getBaseRepository().findAll(), getTClass());
    }

    @Override
    default Paging<T> findAll(PageRequest<T> pageRequest) {
        Example example = genExample(pageRequest.getData());
        Page<T> all = null;
        if (Objects.isNull(example)) {
            all = getBaseRepository().findAll(PageBuilder.toPageable(pageRequest));
        } else {
            all = getBaseRepository().findAll(example, PageBuilder.toPageable(pageRequest));
        }
        return PageBuilder.toPaging(all, getTClass());
    }

    /**
     * 按条件查询多个结果
     */
    @Override
    default List<T> findAllByCondition(T data) {
        Example example = genExample(data);
        List all = null;
        if (Objects.isNull(example)) {
            all = getBaseRepository().findAll();

        } else {
            all = getBaseRepository().findAll(example);

        }
        return MapstructUtils.convert(all, getTClass());
    }

    /**
     * 按条件查询单个结果
     */
    @Override
    default T findOneByCondition(T data) {
        Example example = genExample(data);

        Optional one = getBaseRepository().findOne(example);
        if (one.isPresent()) {
            return (T) MapstructUtils.convert(one.get(), getTClass());
        }
        return null;
    }

    default Example genExample(T data) {
        if (Objects.isNull(data)) {
            return null;
        }
        return Example.of(MapstructUtils.convert(data, getJpaRepositoryClass()),
                ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
    }

}
