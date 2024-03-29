package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class ProductModelCacheEvict {

    @CacheEvict(value = Constants.CACHE_PRODUCT_SCRIPT, key = "#root.method.name+#model")
    public void findByModel(String model) {
    }

}
