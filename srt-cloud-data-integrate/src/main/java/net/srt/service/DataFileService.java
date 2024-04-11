package net.srt.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataFileVO;
import net.srt.query.DataFileQuery;
import net.srt.entity.DataFileEntity;

import java.util.List;

/**
 * 文件表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-16
 */
public interface DataFileService extends BaseService<DataFileEntity> {

    PageResult<DataFileVO> page(DataFileQuery query);

	PageResult<DataFileVO> pageResource(DataFileQuery query);

    void save(DataFileVO vo);

    void update(DataFileVO vo);

    void delete(List<Long> idList);

}
