package net.srt.system.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.system.entity.SysAttachmentEntity;
import net.srt.system.query.SysAttachmentQuery;
import net.srt.system.vo.SysAttachmentVO;

import java.util.List;

/**
 * 附件管理
 *
 * @author 阿沐 babamu@126.com
 */
public interface SysAttachmentService extends BaseService<SysAttachmentEntity> {

    PageResult<SysAttachmentVO> page(SysAttachmentQuery query);

    void save(SysAttachmentVO vo);

    void update(SysAttachmentVO vo);

    void delete(List<Long> idList);
}
