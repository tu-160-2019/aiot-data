package cc.iotkit.manager.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.model.space.SpaceDevice;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 14:46
 */
public interface ISpaceDeviceService {

    List<SpaceDevice> findByHomeIdAndCollect(Long homeId,boolean collect);

    SpaceDevice findByDeviceId(String deviceId);

    SpaceDevice save(SpaceDevice spaceDevice);

    List<SpaceDevice> findByHomeId(Long homeId);

    List<SpaceDevice> findBySpaceId(Long spaceId);

    void deleteById (Long id);

    SpaceDevice findById (Long id);

    Paging<ThingModelMessage> findByTypeAndDeviceIds(List<String> deviceIds, String type,
                                                     String identifier,
                                                     int page, int size);
}
