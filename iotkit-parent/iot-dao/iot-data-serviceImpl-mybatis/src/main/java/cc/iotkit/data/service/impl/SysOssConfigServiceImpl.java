package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysOssConfigMapper;
import cc.iotkit.data.model.TbSysOssConfig;
import cc.iotkit.data.service.SysOssConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service("DBSysOssConfigServiceImpl")
@Primary
public class SysOssConfigServiceImpl extends ServiceImpl<SysOssConfigMapper, TbSysOssConfig> implements SysOssConfigService {
}
