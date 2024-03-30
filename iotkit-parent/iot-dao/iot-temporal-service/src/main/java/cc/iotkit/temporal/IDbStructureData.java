package cc.iotkit.temporal;

import cc.iotkit.model.product.ThingModel;

/**
 * 数据结构接口
 */
public interface IDbStructureData {

    /**
     * 定义物模型，根据物模型定义表
     */
    void defineThingModel(ThingModel thingModel);

    /**
     * 更新物模型定义
     */
    void updateThingModel(ThingModel thingModel);

    /**
     * 初始化数据库结构
     */
    void initDbStructure();

}
