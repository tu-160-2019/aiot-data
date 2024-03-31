package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.AlertConfigMapper;
import cc.iotkit.data.model.TbAlertConfig;
import cc.iotkit.data.service.AlertConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AlertConfigServiceImpl extends ServiceImpl<AlertConfigMapper, TbAlertConfig> implements AlertConfigService {

}
