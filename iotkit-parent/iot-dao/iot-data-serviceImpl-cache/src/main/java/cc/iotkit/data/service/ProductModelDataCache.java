package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.data.manager.IProductModelData;
import cc.iotkit.data.cache.ProductModelCacheEvict;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Qualifier("productModelDataCache")
public class ProductModelDataCache implements IProductModelData {

    @Autowired
    private IProductModelData productModelData;
    @Autowired
    private ProductModelCacheEvict productModelCacheEvict;

    @Override
    @Cacheable(value = Constants.CACHE_PRODUCT_SCRIPT, key = "#root.method.name+#model", unless = "#result == null")
    public ProductModel findByModel(String model) {
        return productModelData.findByModel(model);
    }

    @Override
    public List<ProductModel> findByProductKey(String productKey) {
        return productModelData.findByProductKey(productKey);
    }

    @Override
    public ProductModel findById(String s) {
        return productModelData.findById(s);
    }

    @Override
    public List<ProductModel> findByIds(Collection<String> id) {
        return null;
    }

    @Override
    public ProductModel save(ProductModel data) {
        ProductModel productModel = productModelData.save(data);
        productModelCacheEvict.findByModel(data.getModel());
        return productModel;
    }

    @Override
    public void batchSave(List<ProductModel> data) {

    }

    @Override
    public void deleteById(String s) {
        productModelData.deleteById(s);
    }

    @Override
    public void deleteByIds(Collection<String> strings) {

    }

    @Override
    public long count() {
        return productModelData.count();
    }

    @Override
    public List<ProductModel> findAll() {
        return productModelData.findAll();
    }

    @Override
    public Paging<ProductModel> findAll(PageRequest<ProductModel> pageRequest) {
        return productModelData.findAll(pageRequest);
    }

    @Override
    public List<ProductModel> findAllByCondition(ProductModel data) {
        return null;
    }

    @Override
    public ProductModel findOneByCondition(ProductModel data) {
        return null;
    }

}
