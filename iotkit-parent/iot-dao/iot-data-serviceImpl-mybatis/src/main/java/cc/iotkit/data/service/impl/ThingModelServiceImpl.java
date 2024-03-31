package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.ThingModelMapper;
import cc.iotkit.data.model.TbThingModel;
import cc.iotkit.data.service.ThingModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service("DBThingModelServiceImpl")
@Primary
public class ThingModelServiceImpl extends ServiceImpl<ThingModelMapper, TbThingModel> implements ThingModelService {

    @Override
    public TbThingModel findById(Long id) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbThingModel>()
                .eq(TbThingModel::getId, id));

    }


    @Override
    public TbThingModel findByProductKey(String productKey) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbThingModel>()
                .eq(TbThingModel::getProductKey, productKey));
    }
}
