package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.model.TbProduct;
import cc.iotkit.model.product.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductService extends IService<TbProduct> {
    List<Product> findByCategory(String category);

    Product findByProductKey(String productKey);

    void delByProductKey(String productKey);

    List<Product> findByUid(String uid);

    Paging<Product> findByUid(String uid, int page, int size);

    long countByUid(String uid);

}
