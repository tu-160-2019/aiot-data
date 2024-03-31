package cc.iotkit.data.service;

import cc.iotkit.data.model.TbUserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserInfoService extends IService<TbUserInfo> {
    TbUserInfo findByUid(String uid);

    List<TbUserInfo> findByType(int type);

    List<TbUserInfo> findByTypeAndOwnerId(int type, String ownerId);
}
