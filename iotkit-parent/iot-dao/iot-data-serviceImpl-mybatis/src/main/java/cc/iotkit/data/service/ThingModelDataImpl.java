/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IThingModelData;
import cc.iotkit.data.model.TbThingModel;
import cc.iotkit.model.product.ThingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author sjg
 */
@Primary
@Service
@RequiredArgsConstructor
public class ThingModelDataImpl implements IThingModelData, IJPACommData<ThingModel, Long> {
//public class ThingModelDataImpl implements IThingModelData, IJPACommData<ThingModel, Long, TbThingModel> {

    @Resource
    @Qualifier("DBThingModelServiceImpl")
    private ThingModelService dbThingModelServiceImpl;

    @Override
    public ThingModelService getBaseRepository() {
        return dbThingModelServiceImpl;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbThingModel.class;
    }

    @Override
    public Class getTClass() {
        return ThingModel.class;
    }

    @Override
    public ThingModel findById(Long id) {
        TbThingModel tbThingModel = dbThingModelServiceImpl.findById(id);
        ThingModel thingModel = MapstructUtils.convert(tbThingModel, ThingModel.class);
        if (tbThingModel != null && thingModel != null) {
            thingModel.setModel(JsonUtils.parseObject(tbThingModel.getModel(), ThingModel.Model.class));
        }
        return thingModel;
    }

    @Override
    public ThingModel save(ThingModel data) {
        TbThingModel tbThingModel = MapstructUtils.convert(data, TbThingModel.class);
        Assert.notNull(tbThingModel, "ThingModel is null.");
        tbThingModel.setModel(JsonUtils.toJsonString(data.getModel()));
        dbThingModelServiceImpl.saveOrUpdate(tbThingModel);
        return data;
    }

    @Override
    public void deleteById(Long id) {
        dbThingModelServiceImpl.removeById(id);
    }

    @Override
    public ThingModel findByProductKey(String productKey) {
        TbThingModel tbThingModel = dbThingModelServiceImpl.findByProductKey(productKey);
        ThingModel thingModel = MapstructUtils.convert(tbThingModel, ThingModel.class);
        if (tbThingModel != null && thingModel != null) {
            thingModel.setModel(JsonUtils.parseObject(tbThingModel.getModel(), ThingModel.Model.class));
        }
        return thingModel;
    }
}
