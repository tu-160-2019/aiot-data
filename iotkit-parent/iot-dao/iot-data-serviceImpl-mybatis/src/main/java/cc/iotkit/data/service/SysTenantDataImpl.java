package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysTenantRepository;
import cc.iotkit.data.model.TbSysTenant;
import cc.iotkit.data.system.ISysTenantData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysTenant;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cc.iotkit.data.model.QTbSysTenant.tbSysTenant;

/**
 * @Author：tfd
 * @Date：2023/5/31 9:45
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysTenantDataImpl implements ISysTenantData, IJPACommData<SysTenant, Long> {

    private final SysTenantRepository sysTenantRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return sysTenantRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysTenant.class;
    }

    @Override
    public Class getTClass() {
        return SysTenant.class;
    }


    @Override
    public List<SysTenant> findAllByCondition(SysTenant data) {
        List<TbSysTenant> ret=jpaQueryFactory.selectFrom(tbSysTenant).where(PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(data.getTenantId()),()->tbSysTenant.tenantId.eq(data.getTenantId()))
                .and(StringUtils.isNotBlank(data.getContactUserName()),()->tbSysTenant.contactUserName.like(data.getContactUserName()))
                .and(StringUtils.isNotBlank(data.getContactPhone()),()->tbSysTenant.contactPhone.eq(data.getContactPhone()))
                .and(StringUtils.isNotBlank(data.getCompanyName()),()->tbSysTenant.companyName.like(data.getCompanyName()))
                .and(StringUtils.isNotBlank(data.getLicenseNumber()),()->tbSysTenant.licenseNumber.eq(data.getLicenseNumber()))
                .and(StringUtils.isNotBlank(data.getAddress()),()->tbSysTenant.address.eq(data.getAddress()))
                .and(StringUtils.isNotBlank(data.getIntro()),()->tbSysTenant.intro.eq(data.getIntro()))
                .and(StringUtils.isNotBlank(data.getDomain()),()->tbSysTenant.domain.like(data.getDomain()))
                .and(data.getPackageId() != null,()->tbSysTenant.packageId.eq(data.getPackageId()))
                .and(data.getExpireTime() != null,()->tbSysTenant.expireTime.eq(data.getExpireTime()))
                .and(data.getAccountCount() != null,()->tbSysTenant.accountCount.eq(data.getAccountCount()))
                .and(StringUtils.isNotBlank(data.getStatus()),()->tbSysTenant.status.eq(data.getStatus()))
                .build()).fetch();
        return MapstructUtils.convert(ret, SysTenant.class);
    }

    @Override
    public boolean checkCompanyNameUnique(SysTenant tenant) {
        final TbSysTenant ret = jpaQueryFactory.select(tbSysTenant).from(tbSysTenant)
                .where(PredicateBuilder.instance()
                        .and(tbSysTenant.companyName.eq(tenant.getCompanyName()))
                        .and(Objects.nonNull(tenant.getId()), () -> tbSysTenant.id.ne(tenant.getId()))
                        .build()).fetchOne();
        return Objects.isNull(ret);
    }
}
