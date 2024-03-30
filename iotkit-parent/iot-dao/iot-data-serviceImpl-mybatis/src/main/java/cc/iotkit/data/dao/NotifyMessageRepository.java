package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbNotifyMessage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: 石恒
 * @Date: 2023/5/13 18:36
 * @Description:
 */
@Mapper
public interface NotifyMessageRepository extends BaseMapper<TbNotifyMessage> {

}