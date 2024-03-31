package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.ChannelTemplateMapper;
import cc.iotkit.data.model.TbChannelTemplate;
import cc.iotkit.data.service.ChannelTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ChannelTemplateServiceImpl extends ServiceImpl<ChannelTemplateMapper, TbChannelTemplate> implements ChannelTemplateService {
}
