package cc.iotkit.data.manager;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.product.ThingModel;

public interface IThingModelData extends ICommonData<ThingModel, Long> {

    ThingModel findByProductKey(String productKey);
}
