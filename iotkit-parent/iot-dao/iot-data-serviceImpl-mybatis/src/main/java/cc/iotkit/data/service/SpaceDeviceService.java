package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.model.TbSpaceDevice;
import cc.iotkit.model.space.SpaceDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SpaceDeviceService extends IService<TbSpaceDevice> {
    List<SpaceDevice> findByUidOrderByUseAtDesc(String uid);

    List<SpaceDevice> findByHomeIdAndCollect(Long homeId, boolean collect);

    List<SpaceDevice> findByHomeId(Long homeId);

    List<SpaceDevice> findBySpaceId(Long spaceId);

    List<SpaceDevice> findByUidOrderByAddAtDesc(String uid);

    List<SpaceDevice> findBySpaceIdOrderByAddAtDesc(String spaceId);

    List<SpaceDevice> findByUidAndSpaceIdOrderByAddAtDesc(String uid, String spaceId);

    SpaceDevice findByDeviceId(String deviceId);

    SpaceDevice findByDeviceIdAndUid(String deviceId, String uid);

    List<SpaceDevice> findByUid(String uid);

    Paging<SpaceDevice> findByUid(String uid, int page, int size);

    public void deleteAllBySpaceId(Long spaceId);
}
