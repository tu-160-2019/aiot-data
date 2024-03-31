package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSpace;
import cc.iotkit.model.space.Space;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SpaceService extends IService<TbSpace> {
    List<TbSpace> findByUidOrderByCreateAtDesc(String uid);

    List<TbSpace> findByUidAndHomeIdOrderByCreateAtDesc(String uid, String homeId);

    List<TbSpace> findByHomeId(Long homeId);

    List<TbSpace> findByUid(String uid);

    Page<TbSpace> findByUid(String uid, int page, int size);
}
