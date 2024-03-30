package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbChannel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChannelRepository extends BaseMapper<TbChannel> {
}
