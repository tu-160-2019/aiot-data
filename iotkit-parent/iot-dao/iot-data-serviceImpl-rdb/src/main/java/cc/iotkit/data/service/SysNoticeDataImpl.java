package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysNoticRepository;
import cc.iotkit.data.model.TbSysNotice;
import cc.iotkit.data.system.ISysNoticeData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysNotice;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import static cc.iotkit.data.model.QTbSysNotice.tbSysNotice;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysNoticeDataImpl implements ISysNoticeData, IJPACommData<SysNotice, Long> {

    @Autowired
    private SysNoticRepository baseRepository;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return baseRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysNotice.class;
    }

    @Override
    public Class getTClass() {
        return SysNotice.class;
    }


    @Override
    public Paging<SysNotice> findByConditions(PageRequest<SysNotice> pageRequest) {
        SysNotice data = pageRequest.getData();
        Predicate predicate = buildQueryCondition(data);
        QueryResults<TbSysNotice> tbSysNoticeQueryResults = jpaQueryFactory.select(tbSysNotice).from(tbSysNotice).where(predicate).offset(pageRequest.getOffset()).limit(pageRequest.getPageSize()).fetchResults();
        return new Paging<>(tbSysNoticeQueryResults.getTotal(), MapstructUtils.convert(tbSysNoticeQueryResults.getResults(), SysNotice.class));
    }

    private Predicate buildQueryCondition(SysNotice query) {
        return PredicateBuilder.instance().and(StringUtils.isNotBlank(query.getNoticeTitle()), ()->
                tbSysNotice.noticeTitle.like(query.getNoticeTitle()))
                .and(StringUtils.isNotBlank(query.getNoticeType()), ()->
                        tbSysNotice.noticeType.eq(query.getNoticeType()))
                .and(StringUtils.isNotBlank(query.getStatus()), ()->
                        tbSysNotice.status.eq(query.getStatus()))
                .and(StringUtils.isNotBlank(query.getCreateByName()), ()->(
                        tbSysNotice.createBy.like(query.getCreateByName())))
        .build();
    }
}
