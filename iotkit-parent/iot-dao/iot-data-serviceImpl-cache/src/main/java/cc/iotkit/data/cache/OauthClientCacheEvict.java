package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class OauthClientCacheEvict {

    @CacheEvict(value = Constants.CACHE_OAUTH_CLIENT, key = "#root.method.name+#clientId")
    public void findByClientId(String clientId) {
    }

}
