package cc.iotkit.data.service.impl;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.OauthClientMapper;
import cc.iotkit.data.model.TbOauthClient;
import cc.iotkit.data.service.OauthClientService;
import cc.iotkit.model.OauthClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class OauthClientServiceImpl extends ServiceImpl<OauthClientMapper, TbOauthClient> implements OauthClientService {
    @Override
    public OauthClient findByClientId(String clientId) {
        TbOauthClient tbOauthClient = this.baseMapper.selectOne(new LambdaQueryWrapper<TbOauthClient>()
                .eq(StringUtils.isNotBlank(clientId), TbOauthClient::getClientId, clientId));
        return MapstructUtils.convert(tbOauthClient, OauthClient.class);
    }
}
