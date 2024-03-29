package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.manager.dto.bo.category.CategoryBo;
import cc.iotkit.manager.dto.bo.product.ProductBo;
import cc.iotkit.manager.dto.bo.productmodel.ProductModelBo;
import cc.iotkit.manager.dto.bo.thingmodel.ThingModelBo;
import cc.iotkit.manager.dto.vo.category.CategoryVo;
import cc.iotkit.manager.dto.vo.product.ProductVo;
import cc.iotkit.manager.dto.vo.productmodel.ProductModelVo;
import cc.iotkit.manager.dto.vo.thingmodel.ThingModelVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: jay
 * @Date: 2023/5/30 16:23
 * @Version: V1.0
 * @Description: 产品服务接口
 */
public interface IProductService {
    ProductVo addEntity(ProductBo data);

    boolean updateEntity(ProductBo productBo);

    ProductVo getDetail(String data);

    boolean deleteProduct(String productKey);

    ThingModelVo getThingModelByProductKey(String data);

    boolean saveThingModel(ThingModelBo data);

    boolean deleteThingModel(Long id);

    boolean deleteCategory(String data);

    boolean editCategory(CategoryBo req);

    String uploadImg(String productKey, MultipartFile file);

    Paging<ProductVo> selectPageList(PageRequest<ProductBo> request);

    Paging<CategoryVo> selectCategoryPageList(PageRequest<CategoryBo> request);

    List<CategoryVo> selectCategoryList();

    List<ProductModelVo> getModels(String productKey);

    boolean editProductModel(ProductModelBo productModel);

    boolean deleteProductModel(String id);

    ProductVo findByProductKey(String productKey);
}
