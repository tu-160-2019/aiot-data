package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysOperLog;
import cc.iotkit.data.system.ISysOperLogData;
import cc.iotkit.model.system.SysOperLog;
import cn.hutool.core.util.ArrayUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author：tfd
 * @Date：2023/5/31 15:24
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysOperLogDataImpl implements ISysOperLogData, IJPACommData<SysOperLog, Long, TbSysOperLog> {

    @Resource
    @Qualifier("DBSysOperLogServiceImpl")
    private final SysOperLogService sysOperLogService;

    @Override
    public SysOperLogService getBaseRepository() {
        return sysOperLogService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysOperLog.class;
    }

    @Override
    public Class getTClass() {
        return SysOperLog.class;
    }

    @Override
    public Paging<SysOperLog> findByConditions(String tenantId, String title, Integer businessType, Integer status, int page, int size) {
        return null;
    }

    @Override
    public void deleteByTenantId(String tenantId) {

    }

    @Override
    public Paging<SysOperLog> findAll(PageRequest<SysOperLog> pageRequest) {
        Page<TbSysOperLog> paged = sysOperLogService.findPageByCondition(buildQueryCondition(pageRequest.getData()),
                pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysOperLog.class));
    }

    @Override
    public void deleteAll() {
        sysOperLogService.deleteAll();
    }

    @Override
    public List<SysOperLog> findAllByCondition(SysOperLog data) {
        LambdaQueryWrapper<TbSysOperLog> queryWrapper = buildQueryCondition(data);
        queryWrapper.orderByAsc(TbSysOperLog::getId);
        List<TbSysOperLog> sysOperLogList = sysOperLogService.findByCondition(queryWrapper);
        return MapstructUtils.convert(sysOperLogList, SysOperLog.class);
    }

    private LambdaQueryWrapper<TbSysOperLog> buildQueryCondition(SysOperLog data) {
        return new LambdaQueryWrapper<TbSysOperLog>()
                .eq(data.getBusinessType() != null && data.getBusinessType() > 0, TbSysOperLog::getBusinessType, data.getBusinessType())
                .in(ArrayUtil.isNotEmpty(data.getBusinessTypes()), TbSysOperLog::getBusinessType, data.getBusinessTypes())
                .eq(data.getStatus() != null && data.getStatus() > 0, TbSysOperLog::getStatus, data.getStatus())
                .like(StringUtils.isNotEmpty(data.getTitle()), TbSysOperLog::getTitle, data.getTitle())
                .like(StringUtils.isNotEmpty(data.getOperName()), TbSysOperLog::getOperName, data.getOperName());
    }
}
