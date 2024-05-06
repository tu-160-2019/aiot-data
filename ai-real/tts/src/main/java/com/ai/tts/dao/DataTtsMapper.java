package com.ai.tts.dao;

import com.ai.tts.entity.DataTtsEntity;
import net.srt.framework.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataTtsMapper extends BaseDao<DataTtsEntity> {
}
