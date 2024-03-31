package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysTenant;
import cc.iotkit.data.system.ISysTenantData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysTenant;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.List;
import java.util.Objects;

@Primary
@Service
@RequiredArgsConstructor
public class SysTenantDataImpl implements ISysTenantData, IJPACommData<SysTenant, Long, TbSysTenant> {

    @Qualifier("DBSysTenantServiceImpl")
    private final SysTenantService sysTenantService;

    @Override
    public SysTenantService getBaseRepository() {
        return sysTenantService;
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
        List<TbSysTenant> ret = sysTenantService.list(new LambdaQueryWrapper<TbSysTenant>()
                .eq(StringUtils.isNotBlank(data.getTenantId()), TbSysTenant::getTenantId, data.getTenantId())
                .eq(StringUtils.isNotBlank(data.getContactUserName()), TbSysTenant::getContactUserName, data.getContactUserName())
                .eq(StringUtils.isNotBlank(data.getContactPhone()), TbSysTenant::getContactPhone, data.getContactPhone())
                .eq(StringUtils.isNotBlank(data.getCompanyName()), TbSysTenant::getCompanyName, data.getCompanyName())
                .eq(StringUtils.isNotBlank(data.getLicenseNumber()), TbSysTenant::getLicenseNumber, data.getLicenseNumber())
                .eq(StringUtils.isNotBlank(data.getAddress()), TbSysTenant::getAddress, data.getAddress())
                .eq(StringUtils.isNotBlank(data.getIntro()), TbSysTenant::getIntro, data.getIntro())
                .eq(StringUtils.isNotBlank(data.getDomain()), TbSysTenant::getDomain, data.getDomain())
                .eq(StringUtils.isNotBlank(data.getStatus()), TbSysTenant::getStatus, data.getStatus())
                .eq(Objects.nonNull(data.getPackageId()), TbSysTenant::getPackageId, data.getPackageId())
                .eq(Objects.nonNull(data.getExpireTime()), TbSysTenant::getExpireTime, data.getExpireTime())
                .eq(Objects.nonNull(data.getAccountCount()), TbSysTenant::getAccountCount, data.getAccountCount())
                .eq(StringUtils.isNotBlank(data.getTenantId()), TbSysTenant::getTenantId, data.getTenantId()));
        return MapstructUtils.convert(ret, SysTenant.class);
    }

    @Override
    public boolean checkCompanyNameUnique(SysTenant tenant) {
        final TbSysTenant ret = sysTenantService.getOne(new LambdaQueryWrapper<TbSysTenant>().eq(
                TbSysTenant::getCompanyName, tenant.getCompanyName()
        ));
        return Objects.isNull(ret);
    }
}
