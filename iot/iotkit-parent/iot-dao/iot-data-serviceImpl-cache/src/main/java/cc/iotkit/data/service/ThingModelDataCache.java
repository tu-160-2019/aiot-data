package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.data.manager.IThingModelData;
import cc.iotkit.data.cache.ThingModelCacheEvict;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.product.ThingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Qualifier("thingModelDataCache")
public class ThingModelDataCache implements IThingModelData {

    @Autowired
    private IThingModelData thingModelData;
    @Autowired
    private ThingModelCacheEvict thingModelCacheEvict;

    @Override
    @Cacheable(value = Constants.CACHE_THING_MODEL, key = "#root.method.name+#s", unless = "#result == null")
    public ThingModel findById(Long s) {
        return thingModelData.findById(s);
    }

    @Override
    public List<ThingModel> findByIds(Collection<Long> id) {
        return null;
    }

    @Override
    public ThingModel save(ThingModel data) {
        data = thingModelData.save(data);
        thingModelCacheEvict.findById(data.getId());
        thingModelCacheEvict.findByProductKey(data.getProductKey());
        return data;
    }

    @Override
    public void batchSave(List<ThingModel> data) {

    }

    @Override
    public void deleteById(Long s) {
        thingModelData.deleteById(s);
        thingModelCacheEvict.findById(s);
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {

    }

    @Override
    public long count() {
        return thingModelData.count();
    }

    @Override
    public List<ThingModel> findAll() {
        return thingModelData.findAll();
    }

    @Override
    public Paging<ThingModel> findAll(PageRequest<ThingModel> pageRequest) {
        return thingModelData.findAll(pageRequest);
    }

    @Override
    public List<ThingModel> findAllByCondition(ThingModel data) {
        return null;
    }

    @Override
    public ThingModel findOneByCondition(ThingModel data) {
        return null;
    }

    @Override
    @Cacheable(value = Constants.CACHE_THING_MODEL, key = "#root.method.name+#productKey", unless = "#result == null")
    public ThingModel findByProductKey(String productKey) {
        return thingModelData.findByProductKey(productKey);
    }
}
