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
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.ThingModelRepository;
import cc.iotkit.data.manager.IThingModelData;
import cc.iotkit.data.model.TbThingModel;
import cc.iotkit.model.product.ThingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author sjg
 */
@Primary
@Service
@RequiredArgsConstructor
public class ThingModelDataImpl implements IThingModelData, IJPACommData<ThingModel, Long> {

    @Autowired
    private ThingModelRepository thingModelRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return thingModelRepository;
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
        TbThingModel tbThingModel = thingModelRepository.findById(id).orElse(null);
        ThingModel convert = MapstructUtils.convert(tbThingModel, ThingModel.class);
        if (tbThingModel != null && convert != null) {
            convert.setModel(JsonUtils.parseObject(tbThingModel.getModel(), ThingModel.Model.class));
        }
        return convert;
    }

    @Override
    public ThingModel save(ThingModel data) {
        TbThingModel to = data.to(TbThingModel.class);
        to.setModel(JsonUtils.toJsonString(data.getModel()));
        thingModelRepository.save(to);
        return data;
    }

    @Override
    public void deleteById(Long id) {
        thingModelRepository.deleteById(id);
    }

    @Override
    public ThingModel findByProductKey(String productKey) {
        TbThingModel tbThingModel = thingModelRepository.findByProductKey(productKey).orElse(null);
        ThingModel convert = MapstructUtils.convert(tbThingModel, ThingModel.class);
        if (tbThingModel != null && convert != null) {
            convert.setModel(JsonUtils.parseObject(tbThingModel.getModel(), ThingModel.Model.class));
        }
        return convert;
    }
}
