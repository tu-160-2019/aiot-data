package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.system.ISysNoticeData;
import cc.iotkit.model.system.SysNotice;
import cc.iotkit.system.dto.bo.SysNoticeBo;
import cc.iotkit.system.dto.vo.SysNoticeVo;
import cc.iotkit.system.service.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 公告 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    private final ISysNoticeData sysNoticeData;

    @Override
    public Paging<SysNoticeVo> selectPageNoticeList( PageRequest<SysNoticeBo> query) {
        return sysNoticeData.findByConditions(query.to(SysNotice.class)).to(SysNoticeVo.class);
    }

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNoticeVo selectNoticeById(Long noticeId) {
        return sysNoticeData.findById(noticeId).to(SysNoticeVo.class);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNoticeVo> selectNoticeList(SysNoticeBo notice) {
        List<SysNotice> allByCondition = sysNoticeData.findAllByCondition(notice.to(SysNotice.class));
        return MapstructUtils.convert(allByCondition, SysNoticeVo.class);
    }

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public Long insertNotice(SysNoticeBo bo) {
        return sysNoticeData.save(bo.to(SysNotice.class)).getId();
    }

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public void updateNotice(SysNoticeBo bo) {
        sysNoticeData.save(bo.to(SysNotice.class));
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public void deleteNoticeById(Long noticeId) {
        sysNoticeData.deleteById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public void deleteNoticeByIds(Collection<Long> noticeIds) {
        sysNoticeData.deleteByIds(noticeIds);
    }
}
