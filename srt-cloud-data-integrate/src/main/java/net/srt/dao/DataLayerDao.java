package net.srt.dao;

import net.srt.entity.DataLayerEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 数仓分层
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@Mapper
public interface DataLayerDao extends BaseDao<DataLayerEntity> {

}
