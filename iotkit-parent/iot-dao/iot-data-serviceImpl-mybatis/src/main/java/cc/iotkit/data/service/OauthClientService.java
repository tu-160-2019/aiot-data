package cc.iotkit.data.service;

import cc.iotkit.data.model.TbOauthClient;
import com.baomidou.mybatisplus.extension.service.IService;
import cc.iotkit.model.OauthClient;

public interface OauthClientService extends IService<TbOauthClient> {
    OauthClient findByClientId(String clientId);
}
