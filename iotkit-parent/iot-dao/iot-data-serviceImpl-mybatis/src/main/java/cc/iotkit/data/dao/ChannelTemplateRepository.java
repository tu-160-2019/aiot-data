package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbChannelTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChannelTemplateRepository extends BaseMapper<TbChannelTemplate> {
}
