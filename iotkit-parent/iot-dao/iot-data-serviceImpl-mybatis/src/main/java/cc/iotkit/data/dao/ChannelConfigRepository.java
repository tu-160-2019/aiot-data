package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbChannelConfig;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChannelConfigRepository extends BaseMapper<TbChannelConfig> {
}
