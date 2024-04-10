package cc.iotkit.manager.service.impl;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.manager.ISpaceDeviceData;
import cc.iotkit.manager.service.ISpaceDeviceService;
import cc.iotkit.model.space.SpaceDevice;
import cc.iotkit.temporal.IThingModelMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 14:46
 */
@Service
public class SpaceDeviceServiceImpl implements ISpaceDeviceService {

    @Autowired
    private ISpaceDeviceData spaceDeviceData;

    @Autowired
    private IThingModelMessageData thingModelMessageData;

    @Override
    public List<SpaceDevice> findByHomeIdAndCollect(Long homeId, boolean collect) {
        return spaceDeviceData.findByHomeIdAndCollect(homeId,collect);
    }

    @Override
    public SpaceDevice findByDeviceId(String deviceId) {
        return spaceDeviceData.findByDeviceId(deviceId);
    }

    @Override
    public SpaceDevice save(SpaceDevice spaceDevice) {
        return spaceDeviceData.save(spaceDevice);
    }

    @Override
    public List<SpaceDevice> findByHomeId(Long homeId) {
        return spaceDeviceData.findByHomeId(homeId);
    }

    @Override
    public List<SpaceDevice> findBySpaceId(Long spaceId) {
        return spaceDeviceData.findBySpaceId(spaceId);
    }

    @Override
    public void deleteById(Long id) {
        spaceDeviceData.deleteById(id);
    }

    @Override
    public SpaceDevice findById(Long id) {
        return spaceDeviceData.findById(id);
    }

    @Override
    public Paging<ThingModelMessage> findByTypeAndDeviceIds(List<String> deviceIds, String type, String identifier, int page, int size) {
        if(StringUtils.isEmpty(type)){
            throw new BizException(ErrCode.PARAMS_EXCEPTION);
        }
        return thingModelMessageData.findByTypeAndDeviceIds(deviceIds,type,identifier,page,size);
    }
}
