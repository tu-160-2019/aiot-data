package net.srt.message.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.message.entity.SmsLogEntity;
import net.srt.message.query.SmsLogQuery;
import net.srt.message.vo.SmsLogVO;

/**
 * 短信日志
 *
 * @author 阿沐 babamu@126.com
 */
public interface SmsLogService extends BaseService<SmsLogEntity> {

    PageResult<SmsLogVO> page(SmsLogQuery query);

}
