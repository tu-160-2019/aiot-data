package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CategoryCacheEvict {

    @CacheEvict(value = Constants.CACHE_CATEGORY, key = "#root.method.name+#s")
    public void findById(String s) {
    }

}
