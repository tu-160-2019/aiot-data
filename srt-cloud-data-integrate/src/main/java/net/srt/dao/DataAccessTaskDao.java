package net.srt.dao;

import net.srt.entity.DataAccessTaskEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 数据接入任务记录
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Mapper
public interface DataAccessTaskDao extends BaseDao<DataAccessTaskEntity> {

}
