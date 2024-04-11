package net.srt.dao;

import net.srt.entity.DataProjectUserRelEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* 项目用户关联表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@Mapper
public interface DataProjectUserRelDao extends BaseDao<DataProjectUserRelEntity> {

	DataProjectUserRelEntity getByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
