package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysDictTypeRepository;
import cc.iotkit.data.model.TbSysDictType;
import cc.iotkit.data.system.ISysDictTypeData;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysDictType;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cc.iotkit.data.model.QTbSysDictType.tbSysDictType;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysDictTypeDataImpl implements ISysDictTypeData, IJPACommData<SysDictType, Long> {

    @Autowired
    private SysDictTypeRepository baseRepository;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Paging<SysDictType> findAll(PageRequest<SysDictType> pageRequest) {
        return PageBuilder.toPaging(baseRepository.findAll(buildQueryCondition(pageRequest.getData()), PageBuilder.toPageable(pageRequest))).to(SysDictType.class);
    }

    @Override
    public JpaRepository getBaseRepository() {
        return baseRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysDictType.class;
    }

    @Override
    public Class getTClass() {
        return SysDictType.class;
    }


    @Override
    public List<SysDictType> findByConditions(SysDictType query) {
        List<TbSysDictType> rets=jpaQueryFactory.select(tbSysDictType).from(tbSysDictType)
                .where(buildQueryCondition(query))
                .fetch();
        return MapstructUtils.convert(rets, SysDictType.class);
    }

    @Override
    public Paging<SysDictType> findByConditions(SysDictType query, int page, int size) {
        return null;
    }

    @Override
    public SysDictType findByDicType(String dictType) {
        return null;
    }

    @Override
    public void updateDicType(String dictType, String newType) {

    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        final TbSysDictType ret = jpaQueryFactory.select(tbSysDictType).from(tbSysDictType)
                .where(PredicateBuilder.instance()
                        .and(tbSysDictType.dictType.eq(dictType.getDictType()))
                        .and(Objects.nonNull(dictType.getId()), () -> tbSysDictType.id.ne(dictType.getId()))
                        .build()).fetchOne();
        return Objects.nonNull(ret);
    }

    private Predicate buildQueryCondition(SysDictType dictType) {
        return PredicateBuilder.instance()
                .and(StringUtils.isNotEmpty(dictType.getDictName()), () -> tbSysDictType.dictName.like(dictType.getDictName()))
                .and(StringUtils.isNotEmpty(dictType.getDictType()), () -> tbSysDictType.dictType.like(dictType.getDictType()))
                .and(StringUtils.isNotEmpty(dictType.getStatus()), () -> tbSysDictType.status.eq(dictType.getStatus()))
                .and(StringUtils.isNotEmpty(dictType.getTenantId()), () -> tbSysDictType.tenantId.eq(dictType.getTenantId())).build();
    }


}
