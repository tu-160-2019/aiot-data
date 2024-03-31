package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbDeviceOtaDetail;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceOtaDetailMapper extends BaseMapper<TbDeviceOtaDetail> {
}
