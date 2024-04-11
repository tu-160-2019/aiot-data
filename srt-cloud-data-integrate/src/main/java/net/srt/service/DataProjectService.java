package net.srt.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataProjectVO;
import net.srt.query.DataProjectQuery;
import net.srt.entity.DataProjectEntity;

import java.util.List;

/**
 * 数据项目
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-09-27
 */
public interface DataProjectService extends BaseService<DataProjectEntity> {

    PageResult<DataProjectVO> page(DataProjectQuery query);

    void save(DataProjectVO vo);

    void update(DataProjectVO vo);

	void initDb(DataProjectEntity entity);

	void delete(List<Long> idList);

	void addUser(Long projectId, List<Long> userIds);

	List<DataProjectVO> listProjects();

	void testOnline(DataProjectVO vo);
}
