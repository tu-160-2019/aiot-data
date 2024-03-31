package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbDeviceGroupMapping;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceGroupMappingMapper extends BaseMapper<TbDeviceGroupMapping> {

}
