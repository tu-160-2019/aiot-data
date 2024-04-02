package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysLogininfor;
import cc.iotkit.data.system.ISysLogininforData;
import cc.iotkit.model.system.SysLoginInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/5/31 15:58
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysLogininfoDataImpl implements ISysLogininforData, IJPACommData<SysLoginInfo, Long> {
//public class SysLogininfoDataImpl implements ISysLogininforData, IJPACommData<SysLoginInfo, Long, TbSysLogininfor> {

    @Autowired
    private final SysLogininfoService sysLoginInfoService;


    @Override
    public SysLogininfoService getBaseRepository() {
        return sysLoginInfoService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysLogininfor.class;
    }

    @Override
    public Class getTClass() {
        return SysLoginInfo.class;
    }

    @Override
    public Paging<SysLoginInfo> findAll(PageRequest<SysLoginInfo> pageRequest) {
        Page<TbSysLogininfor> paged = sysLoginInfoService.findByConditions(genPredicate(pageRequest.getData()),
                pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysLoginInfo.class));
    }

    @Override
    public List<SysLoginInfo> findByConditions(SysLoginInfo data) {
        LambdaQueryWrapper<TbSysLogininfor> lambdaQueryWrapper = genPredicate(data);
        lambdaQueryWrapper.orderByDesc(TbSysLogininfor::getId);
        List<TbSysLogininfor> sysLoginInfoList = sysLoginInfoService.findByConditions(lambdaQueryWrapper);
        return MapstructUtils.convert(sysLoginInfoList, SysLoginInfo.class);
    }

    private LambdaQueryWrapper<TbSysLogininfor> genPredicate(SysLoginInfo data) {

        return new LambdaQueryWrapper<TbSysLogininfor>()
                .eq(StringUtils.isNotBlank(data.getIpaddr()), TbSysLogininfor::getIpaddr, data.getIpaddr())
                .eq(StringUtils.isNotBlank(data.getStatus()), TbSysLogininfor::getStatus, data.getStatus())
                .eq(StringUtils.isNotBlank(data.getUserName()), TbSysLogininfor::getUserName, data.getUserName());
    }

    @Override
    public Paging<SysLoginInfo> findByConditions(SysLoginInfo cond, int page, int size) {
        Page<TbSysLogininfor> paged = sysLoginInfoService.findByConditions(genPredicate(cond), page, size);
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysLoginInfo.class));
    }

    @Override
    public void deleteByTenantId(String tenantId) {

    }

    @Override
    public void deleteAll() {
        sysLoginInfoService.deleteAll();
    }

}
