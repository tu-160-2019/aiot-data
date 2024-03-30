package cc.iotkit.data.service;

import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysTenantPackageRepository;
import cc.iotkit.data.model.TbSysTenantPackage;
import cc.iotkit.data.system.ISysTenantPackageData;
import cc.iotkit.model.system.SysTenantPackage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysTenantPackageDataImpl implements ISysTenantPackageData, IJPACommData<SysTenantPackage, Long> {

    @Autowired
    private SysTenantPackageRepository baseRepository;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return baseRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysTenantPackage.class;
    }

    @Override
    public Class getTClass() {
        return SysTenantPackage.class;
    }


}
