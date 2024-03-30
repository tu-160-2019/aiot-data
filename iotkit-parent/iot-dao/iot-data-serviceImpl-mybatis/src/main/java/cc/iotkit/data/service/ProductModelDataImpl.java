package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.ProductModelRepository;
import cc.iotkit.data.manager.IProductModelData;
import cc.iotkit.data.model.TbProductModel;
import cc.iotkit.model.product.ProductModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class ProductModelDataImpl implements IProductModelData, IJPACommData<ProductModel, String> {

    @Autowired
    private ProductModelRepository productModelRepository;


    @Override
    public JpaRepository getBaseRepository() {
        return productModelRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbProductModel.class;
    }

    @Override
    public Class getTClass() {
        return ProductModel.class;
    }

    @Override
    public ProductModel findByModel(String model) {
        return MapstructUtils.convert(productModelRepository.findByModel(model), ProductModel.class);
    }

    @Override
    public List<ProductModel> findByProductKey(String productKey) {
        return MapstructUtils.convert(productModelRepository.findByProductKey(productKey), ProductModel.class);
    }

    @Override
    public ProductModel findById(String s) {
        return MapstructUtils.convert(productModelRepository.findById(s).orElse(null), ProductModel.class);
    }

    @Override
    public List<ProductModel> findByIds(Collection<String> id) {
        return Collections.emptyList();
    }

    @Override
    public ProductModel save(ProductModel data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
        }
        data.setModifyAt(System.currentTimeMillis());
        productModelRepository.save(MapstructUtils.convert(data, TbProductModel.class));
        return null;
    }



}
