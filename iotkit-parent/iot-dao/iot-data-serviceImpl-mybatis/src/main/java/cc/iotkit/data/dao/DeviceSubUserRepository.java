package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceSubUser;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceSubUserRepository extends BaseMapper<TbDeviceSubUser> {

    List<TbDeviceSubUser> findByDeviceId(String deviceId);

}
