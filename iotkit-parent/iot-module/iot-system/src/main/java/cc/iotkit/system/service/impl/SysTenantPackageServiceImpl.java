package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.system.ISysTenantPackageData;
import cc.iotkit.model.system.SysTenantPackage;
import cc.iotkit.system.dto.bo.SysTenantPackageBo;
import cc.iotkit.system.dto.vo.SysTenantPackageVo;
import cc.iotkit.system.service.ISysTenantPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 租户套餐Service业务层处理
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@Service
public class SysTenantPackageServiceImpl implements ISysTenantPackageService {

    private final ISysTenantPackageData sysTenantPackageData;

    /**
     * 查询租户套餐
     */
    @Override
    public SysTenantPackageVo queryById(Long packageId) {
        return sysTenantPackageData.findById(packageId).to(SysTenantPackageVo.class);
    }

    /**
     * 查询租户套餐列表
     */
    @Override
    public Paging<SysTenantPackageVo> queryPageList( PageRequest<SysTenantPackageBo> query) {
        return sysTenantPackageData.findAll(query.to(SysTenantPackage.class)).to(SysTenantPackageVo.class);
    }

    @Override
    public List<SysTenantPackageVo> selectList() {
        List<SysTenantPackage> all = sysTenantPackageData.findAll();
        return MapstructUtils.convert(all,SysTenantPackageVo.class);
    }

    /**
     * 查询租户套餐列表
     */
    @Override
    public List<SysTenantPackageVo> queryList(SysTenantPackageBo bo) {
        List<SysTenantPackage> all = sysTenantPackageData.findAllByCondition(bo.to(SysTenantPackage.class));
        return MapstructUtils.convert(all,SysTenantPackageVo.class);
    }

    /**
     * 新增租户套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysTenantPackageBo bo) {
        sysTenantPackageData.save(bo.to(SysTenantPackage.class));
        return true;
    }

    /**
     * 修改租户套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysTenantPackageBo bo) {
        sysTenantPackageData.save(bo.to(SysTenantPackage.class));
        return true;
    }

    /**
     * 修改套餐状态
     *
     * @param bo 套餐信息
     * @return 结果
     */
    @Override
    public void updatePackageStatus(SysTenantPackageBo bo) {
        sysTenantPackageData.save(bo.to(SysTenantPackage.class));
    }

    /**
     * 批量删除租户套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        sysTenantPackageData.deleteByIds(ids);
        return true;
    }
}
