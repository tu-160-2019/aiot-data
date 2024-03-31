package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysAppMapper;
import cc.iotkit.data.model.TbSysApp;
import cc.iotkit.data.service.SysAppService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, TbSysApp> implements SysAppService {

}
