package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.ProductRepository;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.model.TbProduct;
import cc.iotkit.model.product.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.iotkit.data.model.QTbProduct.tbProduct;

@Primary
@Service
public class ProductDataImpl implements IProductData, IJPACommData<Product, Long> {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return productRepository;
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
        return MapstructUtils.convert(productRepository.findByCategory(category), Product.class);
    }

    @Override
    public Product findByProductKey(String productKey) {
        return MapstructUtils.convert(productRepository.findByProductKey(productKey), Product.class);
    }

    @Override
    public void delByProductKey(String productKey) {
        jpaQueryFactory.delete(tbProduct).where(tbProduct.productKey.eq(productKey)).execute();
    }

    public List<Product> findByUid(String uid) {
        return MapstructUtils.convert(productRepository.findByUid(uid), Product.class);
    }

    public Paging<Product> findByUid(String uid, int page, int size) {
        Page<TbProduct> productPage = productRepository.findByUid(uid,
                Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(productPage.getTotalElements(),
                MapstructUtils.convert(productPage.getContent(), Product.class));
    }

    public long countByUid(String uid) {
        return productRepository.countByUid(uid);
    }

    @Override
    public Product save(Product data) {
        productRepository.save(MapstructUtils.convert(data, TbProduct.class));
        return data;
    }

}
