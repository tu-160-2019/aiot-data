package net.srt.dao;

import net.srt.entity.DataTableEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 数据集成-贴源数据
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-07
*/
@Mapper
public interface DataTableDao extends BaseDao<DataTableEntity> {

}
