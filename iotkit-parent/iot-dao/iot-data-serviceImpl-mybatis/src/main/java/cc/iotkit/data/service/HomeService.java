package cc.iotkit.data.service;

import cc.iotkit.data.model.TbHome;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface HomeService extends IService<TbHome> {
    TbHome findByUidAndCurrent(Long uid, boolean current);

    TbHome checkHomeNameUnique(Long uid, String name);

    List<TbHome> findByUid(Long uid);
}
