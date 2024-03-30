package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysLogininforRepository;
import cc.iotkit.data.model.TbSysLogininfor;
import cc.iotkit.data.system.ISysLogininforData;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysLoginInfo;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.iotkit.data.model.QTbSysLogininfor.tbSysLogininfor;

/**
 * @Author：tfd
 * @Date：2023/5/31 15:58
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysLogininfoDataImpl implements ISysLogininforData, IJPACommData<SysLoginInfo, Long> {

    private final SysLogininforRepository logininfoRepository;


    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return logininfoRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysLogininfor.class;
    }

    @Override
    public Class getTClass() {
        return SysLoginInfo.class;
    }

    @Override
    public Paging<SysLoginInfo> findAll(PageRequest<SysLoginInfo> pageRequest) {
        return PageBuilder.toPaging(logininfoRepository.findAll(genPredicate(pageRequest.getData()), PageBuilder.toPageable(pageRequest))).to(SysLoginInfo.class);
    }

    @Override
    public List<SysLoginInfo> findByConditions(SysLoginInfo data) {
        List<TbSysLogininfor> ret=jpaQueryFactory.selectFrom(tbSysLogininfor).where(genPredicate(data))
                .orderBy(tbSysLogininfor.id.desc()).fetch();
        return MapstructUtils.convert(ret, SysLoginInfo.class);
    }

    private static Predicate genPredicate(SysLoginInfo data) {
        return PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(data.getIpaddr()), () -> tbSysLogininfor.ipaddr.like(data.getIpaddr()))
                .and(StringUtils.isNotBlank(data.getStatus()), () -> tbSysLogininfor.status.eq(data.getStatus()))
                .and(StringUtils.isNotBlank(data.getUserName()), () -> tbSysLogininfor.userName.like(data.getUserName()))
                .build();
    }

    @Override
    public Paging<SysLoginInfo> findByConditions(SysLoginInfo cond, int page, int size) {
        Page<TbSysLogininfor> all = logininfoRepository.findAll(genPredicate(cond), PageBuilder.buildPageable(page, size));
        return PageBuilder.toPaging(all, SysLoginInfo.class);
    }

    @Override
    public void deleteByTenantId(String tenantId) {

    }

    @Override
    public void deleteAll() {
        logininfoRepository.deleteAll();
    }

}
