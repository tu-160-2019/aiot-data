package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysTenantPackageMapper;
import cc.iotkit.data.model.TbSysTenantPackage;
import cc.iotkit.data.service.SysTenantPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("DBSysTenantPackageServiceImpl")
@Primary
public class SysTenantPackageServiceImpl extends ServiceImpl<SysTenantPackageMapper, TbSysTenantPackage> implements SysTenantPackageService {

}
