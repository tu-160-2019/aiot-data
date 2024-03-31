package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbDeviceInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceInfoMapper extends BaseMapper<TbDeviceInfo> {

    List<TbDeviceInfo> findByProductNodeType(@Param("uid") String uid, @Param("nodeType") Integer nodeType);

}
