package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.AlertRecordRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IAlertRecordData;
import cc.iotkit.data.model.TbAlertRecord;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.alert.AlertRecord;
import cc.iotkit.model.system.SysLoginInfo;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import static cc.iotkit.data.model.QTbAlertRecord.tbAlertRecord;

@Primary
@Service
public class AlertRecordDataImpl implements IAlertRecordData, IJPACommData<AlertRecord, Long> {

    @Autowired
    private AlertRecordRepository alertRecordRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return alertRecordRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbAlertRecord.class;
    }

    @Override
    public Class getTClass() {
        return AlertRecord.class;
    }

    private static Predicate genPredicate(AlertRecord data) {
        return PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(data.getName()), () -> tbAlertRecord.name.like(data.getName()))
                .and(StringUtils.isNotBlank(data.getLevel()), () -> tbAlertRecord.level.eq(data.getLevel()))
                .build();
    }

    @Override
    public Paging<AlertRecord> selectAlertConfigPage(PageRequest<AlertRecord> request) {
        QueryResults<TbAlertRecord> results = jpaQueryFactory.selectFrom(tbAlertRecord).where(genPredicate(request.getData()))
                .orderBy(tbAlertRecord.id.desc())
                .limit(request.getPageSize())
                .offset(request.getOffset()).fetchResults();
        return new Paging<>(results.getTotal(), results.getResults()).to(AlertRecord.class);
    }
}
