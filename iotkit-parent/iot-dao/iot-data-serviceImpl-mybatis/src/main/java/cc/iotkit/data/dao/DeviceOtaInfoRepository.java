package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceOtaInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceOtaInfoRepository extends BaseMapper<TbDeviceOtaInfo> {
}
