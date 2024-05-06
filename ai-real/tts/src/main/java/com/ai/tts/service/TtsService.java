package com.ai.tts.service;

import com.ai.tts.entity.DataTtsEntity;
import com.ai.tts.query.DataTtsQuery;
import com.ai.tts.vo.DataTtsVo;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;

import java.util.List;

public interface TtsService extends BaseService<DataTtsEntity> {

    PageResult<DataTtsVo> page(DataTtsQuery query);

    void delete(List<Long> idList);

    public void sv2tts(String text);
}
