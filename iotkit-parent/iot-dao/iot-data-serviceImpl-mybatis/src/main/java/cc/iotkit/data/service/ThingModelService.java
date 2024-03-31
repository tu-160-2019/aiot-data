package cc.iotkit.data.service;

import cc.iotkit.data.model.TbThingModel;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ThingModelService  extends IService<TbThingModel> {
    TbThingModel findById(Long id);

    TbThingModel findByProductKey(String productKey);
}
