package net.srt.dao;

import net.srt.entity.DataAccessTaskDetailEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 数据接入-同步记录详情
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-28
*/
@Mapper
public interface DataAccessTaskDetailDao extends BaseDao<DataAccessTaskDetailEntity> {

}
