package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IAlertRecordData;
import cc.iotkit.data.model.TbAlertRecord;
import cc.iotkit.model.alert.AlertRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

@Primary
@Service
public class AlertRecordDataImpl implements IAlertRecordData, IJPACommData<AlertRecord, Long> {
//public class AlertRecordDataImpl implements IAlertRecordData, IJPACommData<AlertRecord, Long, TbAlertRecord> {

    @Autowired
    private AlertRecordService alertRecordService;

    @Override
    public AlertRecordService getBaseRepository() {
        return alertRecordService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbAlertRecord.class;
    }

    @Override
    public Class getTClass() {
        return AlertRecord.class;
    }

//    private static Predicate genPredicate(AlertRecord data) {
//        return PredicateBuilder.instance()
//                .and(StringUtils.isNotBlank(data.getName()), () -> tbAlertRecord.name.like(data.getName()))
//                .and(StringUtils.isNotBlank(data.getLevel()), () -> tbAlertRecord.level.eq(data.getLevel()))
//                .build();
//    }

    @Override
    public Paging<AlertRecord> selectAlertConfigPage(PageRequest<AlertRecord> request) {
//        QueryResults<TbAlertRecord> results = jpaQueryFactory.selectFrom(tbAlertRecord).where(genPredicate(request.getData()))
//                .orderBy(tbAlertRecord.id.desc())
//                .limit(request.getPageSize())
//                .offset(request.getOffset()).fetchResults();
//        return new Paging<>(results.getTotal(), results.getResults()).to(AlertRecord.class);
        return null;
    }
}
