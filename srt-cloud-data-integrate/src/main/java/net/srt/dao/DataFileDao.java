package net.srt.dao;

import net.srt.entity.DataFileEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* 文件表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-16
*/
@Mapper
public interface DataFileDao extends BaseDao<DataFileEntity> {

	List<DataFileEntity> getResourceList(Map<String, Object> params);
}
