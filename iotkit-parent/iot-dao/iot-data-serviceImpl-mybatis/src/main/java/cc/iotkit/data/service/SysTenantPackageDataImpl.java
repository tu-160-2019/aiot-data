package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysTenantPackage;
import cc.iotkit.data.system.ISysTenantPackageData;
import cc.iotkit.model.system.SysTenantPackage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysTenantPackageDataImpl implements ISysTenantPackageData, IJPACommData<SysTenantPackage, Long, TbSysTenantPackage> {

    @Resource
    @Qualifier("DBSysTenantPackageServiceImpl")
    private SysTenantPackageService sysTenantPackageService;

    @Override
    public SysTenantPackageService getBaseRepository() {
        return sysTenantPackageService;
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
