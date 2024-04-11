package net.srt.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataLayerVO;
import net.srt.query.DataLayerQuery;
import net.srt.entity.DataLayerEntity;

import java.util.List;

/**
 * 数仓分层
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
public interface DataLayerService extends BaseService<DataLayerEntity> {

    PageResult<DataLayerVO> page(DataLayerQuery query);

    void save(DataLayerVO vo);

    void update(DataLayerVO vo);

    void delete(List<Long> idList);
}
