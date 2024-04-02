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

    @Qualifier("DBThingModelServiceImpl")
    private ThingModelService thingModelService;

    @Override
    public ThingModelService getBaseRepository() {
        return thingModelService;
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
        TbThingModel tbThingModel = thingModelService.findById(id);
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
        thingModelService.save(tbThingModel);
        return data;
    }

    @Override
    public void deleteById(Long id) {
        thingModelService.removeById(id);
    }

    @Override
    public ThingModel findByProductKey(String productKey) {
        TbThingModel tbThingModel = thingModelService.findByProductKey(productKey);
        ThingModel thingModel = MapstructUtils.convert(tbThingModel, ThingModel.class);
        if (tbThingModel != null && thingModel != null) {
            thingModel.setModel(JsonUtils.parseObject(tbThingModel.getModel(), ThingModel.Model.class));
        }
        return thingModel;
    }
}
