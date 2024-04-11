package net.srt.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataAccessTaskDetailVO;
import net.srt.query.DataAccessTaskDetailQuery;
import net.srt.entity.DataAccessTaskDetailEntity;

import java.util.List;

/**
 * 数据接入-同步记录详情
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-28
 */
public interface DataAccessTaskDetailService extends BaseService<DataAccessTaskDetailEntity> {

    PageResult<DataAccessTaskDetailVO> page(DataAccessTaskDetailQuery query);

    void save(DataAccessTaskDetailVO vo);

    void update(DataAccessTaskDetailVO vo);

    void delete(List<Long> idList);

	void deleteByTaskId(List<Long> idList);

	void deleteByAccessId(Long id);

}
