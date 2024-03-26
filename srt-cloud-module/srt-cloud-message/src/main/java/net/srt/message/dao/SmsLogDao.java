package net.srt.message.dao;

import net.srt.framework.mybatis.dao.BaseDao;
import net.srt.message.entity.SmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 短信日志
*
* @author 阿沐 babamu@126.com
*/
@Mapper
public interface SmsLogDao extends BaseDao<SmsLogEntity> {

}
