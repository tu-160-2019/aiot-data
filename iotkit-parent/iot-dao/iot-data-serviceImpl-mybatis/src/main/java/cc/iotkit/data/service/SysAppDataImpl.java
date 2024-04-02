package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysApp;
import cc.iotkit.data.system.ISysAppData;
import cc.iotkit.model.system.SysApp;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 数据实现接口
 *
 * @author Lion Li
 * @date 2023-08-10
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysAppDataImpl implements ISysAppData, IJPACommData<SysApp, Long> {
//public class SysAppDataImpl implements ISysAppData, IJPACommData<SysApp, Long, TbSysApp> {

    @Resource()
    @Qualifier("DBSysAppServiceImpl")
    private final SysAppService baseService;

//    @Override
//    public Paging<SysApp> findAll(PageRequest<SysApp> pageRequest) {
//        return PageBuilder.toPaging(baseRepository.findAll(buildQueryCondition(pageRequest.getData()), PageBuilder.toPageable(pageRequest))).to(SysApp.class);
//    }

//    private Predicate buildQueryCondition(SysApp bo) {
//        PredicateBuilder builder = PredicateBuilder.instance();
//        if(Objects.nonNull(bo)) {
//
//                        builder.and(StringUtils.isNotBlank(bo.getId()), () -> tbSysApp.ID.eq(bo.getId()));
//                        builder.and(StringUtils.isNotBlank(bo.getTenantId()), () -> tbSysApp.tenantId.eq(bo.getTenantId()));
//                        builder.and(StringUtils.isNotBlank(bo.getAppId()), () -> tbSysApp.appId.eq(bo.getAppId()));
//                        builder.and(StringUtils.isNotBlank(bo.getAppSecret()), () -> tbSysApp.appSecret.eq(bo.getAppSecret()));
//                        builder.and(StringUtils.isNotBlank(bo.getAppType()), () -> tbSysApp.appType.eq(bo.getAppType()));
//                        builder.and(StringUtils.isNotBlank(bo.getRemark()), () -> tbSysApp.REMARK.eq(bo.getRemark()));
//        }
//        return builder.build();
//    }

    @Override
    public SysAppService getBaseRepository() {
        return baseService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysApp.class;
    }

    @Override
    public Class getTClass() {
        return SysApp.class;
    }

    @Override
    public SysApp findByAppId(String appId) {
        TbSysApp ret=baseService.getOne(new LambdaQueryWrapper<TbSysApp>()
                .eq(TbSysApp::getAppId, appId));
        if(ObjectUtil.isNotNull(ret)){
            return MapstructUtils.convert(ret,SysApp.class);
        }else{
            return null;
        }
    }
}
