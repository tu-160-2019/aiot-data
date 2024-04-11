package net.srt.dao;

import net.srt.entity.DataProjectEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* 数据项目
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-09-27
*/
@Mapper
public interface DataProjectDao extends BaseDao<DataProjectEntity> {

	/**
	 * 查看当前用户拥有的项目
	 * @param userId
	 * @return
	 */
	List<DataProjectEntity> listProjects(@Param("userId") Long userId);

}
