package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbChannel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChannelMapper extends BaseMapper<TbChannel> {
}
