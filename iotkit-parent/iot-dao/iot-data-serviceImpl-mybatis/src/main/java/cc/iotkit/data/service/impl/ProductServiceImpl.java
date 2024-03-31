package cc.iotkit.data.service.impl;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.ProductMapper;
import cc.iotkit.data.model.TbProduct;
import cc.iotkit.data.service.ProductService;
import cc.iotkit.model.product.Product;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBProductServiceImpl")
@Primary
public class ProductServiceImpl extends ServiceImpl<ProductMapper, TbProduct> implements ProductService {

    @Override
    public List<Product> findByCategory(String category) {
        List<TbProduct> productList = this.baseMapper.selectList(new LambdaQueryWrapper<TbProduct>()
                .eq(StringUtils.isNotBlank(category), TbProduct::getCategory, category));
        return MapstructUtils.convert(productList, Product.class);
    }

    @Override
    public Product findByProductKey(String productKey) {
        TbProduct tbProduct = this.baseMapper.selectOne(new LambdaQueryWrapper<TbProduct>()
                .eq(StringUtils.isNotBlank(productKey), TbProduct::getProductKey, productKey));
        return MapstructUtils.convert(tbProduct, Product.class);
    }

    @Override
    public void delByProductKey(String productKey) {
        this.baseMapper.delete(new LambdaQueryWrapper<TbProduct>()
                .eq(StringUtils.isNotBlank(productKey), TbProduct::getProductKey, productKey));
    }

    @Override
    public List<Product> findByUid(String uid) {
        List<TbProduct> productList = this.baseMapper.selectList(new LambdaQueryWrapper<TbProduct>()
                .eq(StringUtils.isNotBlank(uid), TbProduct::getUid, uid));
        return MapstructUtils.convert(productList, Product.class);
    }

    @Override
    public Paging<Product> findByUid(String uid, int page, int size) {
        Page<TbProduct> rowPage = new Page<>(page, size);
        Page<TbProduct> deviceGroupPage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbProduct>().eq(TbProduct::getUid, uid));

        Paging<Product> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(deviceGroupPage.getRecords(), Product.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }

    @Override
    public long countByUid(String uid) {
        return this.baseMapper.selectCount(new LambdaQueryWrapper<TbProduct>()
                .eq(StringUtils.isNotBlank(uid), TbProduct::getUid, uid));
    }
}
