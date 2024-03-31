package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysTenantMapper;
import cc.iotkit.data.model.TbSysTenant;
import cc.iotkit.data.service.SysTenantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service("DBSysTenantServiceImpl")
@Primary
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, TbSysTenant> implements SysTenantService {
}
