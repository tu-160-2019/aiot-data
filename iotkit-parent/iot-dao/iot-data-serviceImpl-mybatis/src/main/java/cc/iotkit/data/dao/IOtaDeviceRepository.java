package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbOtaDevice;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IOtaDeviceRepository extends BaseMapper<TbOtaDevice> {
}
