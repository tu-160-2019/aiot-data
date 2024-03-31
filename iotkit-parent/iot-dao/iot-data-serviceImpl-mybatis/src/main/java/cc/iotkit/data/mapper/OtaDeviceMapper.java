package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbOtaDevice;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtaDeviceMapper extends BaseMapper<TbOtaDevice> {
}
