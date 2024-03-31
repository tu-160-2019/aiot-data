package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbDeviceOtaInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceOtaInfoMapper extends BaseMapper<TbDeviceOtaInfo> {
}
