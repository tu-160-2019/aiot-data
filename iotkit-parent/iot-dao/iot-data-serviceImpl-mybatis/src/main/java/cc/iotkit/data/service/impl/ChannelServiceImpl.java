package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.ChannelMapper;
import cc.iotkit.data.model.TbChannel;
import cc.iotkit.data.service.ChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, TbChannel> implements ChannelService {
}
