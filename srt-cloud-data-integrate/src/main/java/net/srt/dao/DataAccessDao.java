package net.srt.dao;

import net.srt.entity.DataAccessEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* 数据集成-数据接入
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Mapper
public interface DataAccessDao extends BaseDao<DataAccessEntity> {

	void updateStartInfo(@Param("dataAccessId") Long dataAccessId);

	void updateEndInfo(@Param("dataAccessId")Long dataAccessId, @Param("runStatus") Integer runStatus, @Param("nextRunTime") Date nextRunTime);

	void changeStatus(@Param("id") Long id, @Param("status") Integer status, @Param("releaseTime") Date releaseTime, @Param("releaseUserId") Long releaseUserId);
}
