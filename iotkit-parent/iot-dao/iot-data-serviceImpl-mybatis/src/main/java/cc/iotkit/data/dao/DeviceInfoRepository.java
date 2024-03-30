package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceInfoRepository extends BaseMapper<TbDeviceInfo> {

    TbDeviceInfo findByDeviceId(String deviceId);

    List<TbDeviceInfo> findByParentId(String parentId);

    TbDeviceInfo findByDeviceName(String deviceName);

}
