package net.srt.dao;

import net.srt.entity.DataAccessIncreaseLogEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 数据集成-数据增量接入日志
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2024-02-26
*/
@Mapper
public interface DataAccessIncreaseLogDao extends BaseDao<DataAccessIncreaseLogEntity> {

}
