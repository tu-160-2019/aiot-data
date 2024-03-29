package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class ProductCacheEvict {

    @CacheEvict(value = Constants.CACHE_PRODUCT, key = "#root.method.name+#id")
    public void findById(Long id) {
    }

    @CacheEvict(value = Constants.CACHE_PRODUCT, key = "#root.method.name+#productKey")
    public void findByProductKey(String productKey) {
    }

}
