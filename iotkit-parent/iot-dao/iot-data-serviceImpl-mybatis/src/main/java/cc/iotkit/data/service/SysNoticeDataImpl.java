package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.model.TbSysNotice;
import cc.iotkit.data.system.ISysNoticeData;

import cc.iotkit.model.system.SysNotice;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Qualifier;


import javax.annotation.Resource;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysNoticeDataImpl implements ISysNoticeData, IJPACommData<SysNotice, Long> {
//public class SysNoticeDataImpl implements ISysNoticeData, IJPACommData<SysNotice, Long, TbSysNotice> {

    @Resource
    @Qualifier("DBSysNoticeServiceImpl")
    private SysNoticeService sysNoticeService;


    @Override
    public SysNoticeService getBaseRepository() {
        return sysNoticeService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysNotice.class;
    }

    @Override
    public Class getTClass() {
        return SysNotice.class;
    }


    @Override
    public Paging<SysNotice> findByConditions(PageRequest<SysNotice> pageRequest) {
        LambdaQueryWrapper<TbSysNotice> wrapper = buildQueryCondition(pageRequest.getData());
        Page<TbSysNotice> sysNoticePage = sysNoticeService.findByConditions(wrapper, pageRequest.getPageNum(), pageRequest.getPageSize());
        Paging<SysNotice> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(sysNoticePage.getRecords(), SysNotice.class));
        paging.setTotal(sysNoticePage.getTotal());
        return paging;
    }

    private LambdaQueryWrapper<TbSysNotice> buildQueryCondition(SysNotice query) {
        return new LambdaQueryWrapper<TbSysNotice>()
                .like(StringUtils.isNotBlank(query.getNoticeTitle()), TbSysNotice::getNoticeTitle, query.getNoticeTitle())
                .eq(StringUtils.isNotBlank(query.getNoticeType()), TbSysNotice::getNoticeType, query.getNoticeType())
                .eq(StringUtils.isNotBlank(query.getStatus()), TbSysNotice::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getCreateByName()), TbSysNotice::getCreateBy, query.getCreateByName());

    }
}
