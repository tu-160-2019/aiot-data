package net.srt.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataAccessTaskVO;
import net.srt.query.DataAccessTaskQuery;
import net.srt.entity.DataAccessTaskEntity;

import java.util.List;

/**
 * 数据接入任务记录
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
public interface DataAccessTaskService extends BaseService<DataAccessTaskEntity> {

    PageResult<DataAccessTaskVO> page(DataAccessTaskQuery query);

    void save(DataAccessTaskVO vo);

    void update(DataAccessTaskVO vo);

    void delete(List<Long> idList);

	void deleteByAccessId(Long id);

	void dealNotFinished();
}
