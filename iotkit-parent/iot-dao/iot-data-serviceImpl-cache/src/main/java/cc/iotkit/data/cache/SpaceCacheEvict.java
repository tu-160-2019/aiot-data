package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class SpaceCacheEvict {

    @CacheEvict(value = Constants.CACHE_SPACE, key = "#root.method.name+#s")
    public void findById(Long s) {
    }

}
