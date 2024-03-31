package cc.iotkit.data.service.impl;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.ProductModelMapper;
import cc.iotkit.data.model.TbProductModel;
import cc.iotkit.data.service.ProductModelService;
import cc.iotkit.model.product.ProductModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Primary
public class ProductModelServiceImpl extends ServiceImpl<ProductModelMapper, TbProductModel> implements ProductModelService {
    @Override
    public ProductModel findByModel(String model) {
        TbProductModel tbProductModel = this.baseMapper.selectOne(new LambdaQueryWrapper<TbProductModel>()
                .eq(StringUtils.isNotBlank(model), TbProductModel::getModel, model));
        return MapstructUtils.convert(tbProductModel, ProductModel.class);
    }

    @Override
    public List<ProductModel> findByProductKey(String productKey) {
        List<TbProductModel> productModelList = this.baseMapper.selectList(new LambdaQueryWrapper<TbProductModel>()
                .eq(StringUtils.isNotBlank(productKey), TbProductModel::getProductKey, productKey));
        return MapstructUtils.convert(productModelList, ProductModel.class);
    }
}
