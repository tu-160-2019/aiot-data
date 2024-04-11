package net.srt.dao;

import net.srt.entity.DataFileCategoryEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 文件分组表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-12
*/
@Mapper
public interface DataFileCategoryDao extends BaseDao<DataFileCategoryEntity> {

}
