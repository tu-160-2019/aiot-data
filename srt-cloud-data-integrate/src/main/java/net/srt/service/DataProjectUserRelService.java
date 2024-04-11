package net.srt.service;

import net.srt.entity.DataProjectUserRelEntity;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataProjectUserRelVO;

import java.util.List;

/**
 * 项目用户关联表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
public interface DataProjectUserRelService extends BaseService<DataProjectUserRelEntity> {

    void save(DataProjectUserRelVO vo);

    void update(DataProjectUserRelVO vo);

    void delete(List<Long> idList);

	DataProjectUserRelEntity getByProjectIdAndUserId(Long projectId, Long userId);

}
