package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.model.TbProduct;
import cc.iotkit.model.product.Product;

import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

@Primary
@Service
public class ProductDataImpl implements IProductData, IJPACommData<Product, Long> {
//public class ProductDataImpl implements IProductData, IJPACommData<Product, Long, TbProduct> {

    @Resource
    @Qualifier("DBProductServiceImpl")
    private ProductService productService;

    @Override
    public ProductService getBaseRepository() {
        return productService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbProduct.class;
    }

    @Override
    public Class getTClass() {
        return Product.class;
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productService.findByCategory(category);
    }

    @Override
    public Product findByProductKey(String productKey) {
        return productService.findByProductKey(productKey);
    }

    @Override
    public void delByProductKey(String productKey) {
        productService.delByProductKey(productKey);
    }

    public List<Product> findByUid(String uid) {
        return productService.findByUid(uid);
    }

    public Paging<Product> findByUid(String uid, int page, int size) {
        return productService.findByUid(uid, page, size);
    }

    public long countByUid(String uid) {
        return productService.countByUid(uid);
    }

    @Override
    public Product save(Product data) {
        productService.saveOrUpdate(MapstructUtils.convert(data, TbProduct.class));
        return data;
    }

}
