package net.srt.dao;

import net.srt.entity.DataDatabaseEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* 数据集成-数据库管理
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-09
*/
@Mapper
public interface DataDatabaseDao extends BaseDao<DataDatabaseEntity> {

	void changeStatusById(@Param("id") Long id, @Param("status") Integer status);
}
