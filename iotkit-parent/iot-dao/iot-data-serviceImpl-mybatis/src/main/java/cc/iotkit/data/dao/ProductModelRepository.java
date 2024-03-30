package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbProductModel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductModelRepository extends BaseMapper<TbProductModel> {

    TbProductModel findByModel(String model);

    List<TbProductModel> findByProductKey(String productKey);

}
