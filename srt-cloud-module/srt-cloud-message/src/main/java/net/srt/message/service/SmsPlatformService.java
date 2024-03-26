package net.srt.message.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.message.entity.SmsPlatformEntity;
import net.srt.message.query.SmsPlatformQuery;
import net.srt.message.sms.config.SmsConfig;
import net.srt.message.vo.SmsPlatformVO;

import java.util.List;

/**
 * 短信平台
 *
 * @author 阿沐 babamu@126.com
 */
public interface SmsPlatformService extends BaseService<SmsPlatformEntity> {

    PageResult<SmsPlatformVO> page(SmsPlatformQuery query);

    /**
     * 启用的短信平台列表
     */
    List<SmsConfig> listByEnable();

    void save(SmsPlatformVO vo);

    void update(SmsPlatformVO vo);

    void delete(List<Long> idList);

}
