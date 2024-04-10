package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IProductModelData;
import cc.iotkit.data.model.TbProductModel;
import cc.iotkit.model.product.ProductModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class ProductModelDataImpl implements IProductModelData, IJPACommData<ProductModel, String> {
//public class ProductModelDataImpl implements IProductModelData, IJPACommData<ProductModel, String, TbProductModel> {

    @Autowired
    private ProductModelService productModelService;


    @Override
    public ProductModelService getBaseRepository() {
        return productModelService;
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
        return productModelService.findByModel(model);
    }

    @Override
    public List<ProductModel> findByProductKey(String productKey) {
        return productModelService.findByProductKey(productKey);
    }

    @Override
    public ProductModel findById(String s) {
        return MapstructUtils.convert(productModelService.getById(s), ProductModel.class);
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
        productModelService.saveOrUpdate(MapstructUtils.convert(data, TbProductModel.class));
        return null;
    }



}
