package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.NotifyMessageMapper;
import cc.iotkit.data.model.TbNotifyMessage;
import cc.iotkit.data.service.NotifyMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class NotifyMessageServiceImpl extends ServiceImpl<NotifyMessageMapper, TbNotifyMessage> implements NotifyMessageService {
}
