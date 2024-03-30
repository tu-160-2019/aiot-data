package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.CacheNames;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.constant.TenantConstants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.enums.UserType;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.system.*;
import cc.iotkit.model.system.*;
import cc.iotkit.system.dto.bo.SysTenantBo;
import cc.iotkit.system.dto.vo.SysTenantVo;
import cc.iotkit.system.service.ISysTenantService;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.github.yitter.idgen.YitIdHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户Service业务层处理
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl implements ISysTenantService {

    private final ISysTenantData sysTenantData;

    private final ISysTenantPackageData sysTenantPackageData;

    private final ISysUserData sysUserData;

    private final ISysRoleData sysRoleData;

    private final ISysRoleMenuData sysRoleMenuData;

    private final ISysDeptData sysDeptData;

    private final ISysRoleDeptData sysRoleDeptData;

    private final ISysUserRoleData sysUserRoleData;

    private final ISysDictTypeData sysDictTypeData;

    private final ISysDictData sysDictData;

    private final ISysConfigData sysConfigData;

    @Override
    public SysTenantVo queryById(Long id) {
        return sysTenantData.findById(id).to(SysTenantVo.class);
    }

    /**
     * 基于租户ID查询租户
     */
    @Cacheable(cacheNames = CacheNames.SYS_TENANT, key = "#tenantId")
    @Override
    public SysTenantVo queryByTenantId(String tenantId) {
        SysTenant sysTenant = new SysTenant();
        sysTenant.setTenantId(tenantId);
        SysTenant tenant = sysTenantData.findOneByCondition(sysTenant);
        return MapstructUtils.convert(tenant,SysTenantVo.class);
    }

    @Override
    public Paging<SysTenantVo> queryPageList(PageRequest<SysTenantBo> query) {
        return sysTenantData.findAll(query.to(SysTenant.class)).to(SysTenantVo.class);
    }

    @Override
    public List<SysTenantVo> queryList(SysTenantBo bo) {
        return MapstructUtils.convert(sysTenantData.findAllByCondition(bo.to(SysTenant.class)),SysTenantVo.class);
    }

    @Override
    public void insertByBo(SysTenantBo bo) {
        bo.setTenantId(YitIdHelper.nextId()+"");
        SysTenant sysTenant=sysTenantData.save(bo.to(SysTenant.class));
        // 根据套餐创建角色
        Long roleId = createTenantRole(sysTenant.getTenantId(), bo.getPackageId());

        // 创建部门: 公司名是部门名称
        SysDept dept = new SysDept();
        dept.setTenantId(sysTenant.getTenantId());
        dept.setDeptName(bo.getCompanyName());
        dept.setParentId(0L);
        dept.setAncestors("0");
        dept.setPhone(bo.getContactPhone());
        dept.setStatus(TenantConstants.NORMAL);
        SysDept retDept =sysDeptData.save(dept);
        Long deptId = retDept.getId();

        // 角色和部门关联表
        SysRoleDept roleDept = new SysRoleDept();
        roleDept.setRoleId(roleId);
        roleDept.setDeptId(deptId);
        sysRoleDeptData.save(roleDept);

        // 创建系统用户
        SysUser user = new SysUser();
        user.setTenantId(sysTenant.getTenantId());
        user.setUserName(bo.getUsername());
        user.setNickName(bo.getUsername());
        user.setPassword(BCrypt.hashpw(bo.getPassword()));
        user.setDeptId(deptId);
        user.setUserType(UserType.SYS_USER.getUserType());
        user.setStatus(TenantConstants.NORMAL);
        user.setRemark(TenantConstants.TENANT_ADMIN_ROLE_NAME);
        user.setPhonenumber(bo.getContactPhone());
        SysUser retUser=sysUserData.save(user);

        //新增系统用户后，默认当前用户为部门的负责人
        SysDept updateDept =sysDeptData.findById(retDept.getId());
        updateDept.setLeader(retUser.getUserName());
        sysDeptData.save(updateDept);

        // 用户和角色关联表
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(retUser.getId());
        userRole.setRoleId(roleId);
        sysUserRoleData.save(userRole);

        String defaultTenantId = TenantConstants.DEFAULT_TENANT_ID;
        SysDictType querySysDictType=new SysDictType();
        querySysDictType.setTenantId(defaultTenantId);
        List<SysDictType> dictTypeList = sysDictTypeData.findByConditions(querySysDictType);
        SysDictData querySysDictData=new SysDictData();
        querySysDictData.setTenantId(defaultTenantId);
        List<SysDictData> dictDataList = sysDictData.findByConditions(querySysDictData);
        for (SysDictType dictType : dictTypeList) {
            dictType.setId(null);
            dictType.setTenantId(sysTenant.getTenantId());
        }
        for (SysDictData dictData : dictDataList) {
            dictData.setId(null);
            dictData.setTenantId(sysTenant.getTenantId());
        }

        sysDictTypeData.batchSave(dictTypeList);
        sysDictData.batchSave(dictDataList);
        SysConfig querySysConfig=new SysConfig();
        querySysConfig.setTenantId(defaultTenantId);
        List<SysConfig> sysConfigList = sysConfigData.findAllByCondition(querySysConfig);

        for (SysConfig config : sysConfigList) {
            config.setId(null);
            config.setTenantId(sysTenant.getTenantId());
        }
        sysConfigData.batchSave(sysConfigList);
    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageId 租户套餐id
     * @return 角色id
     */
    private Long createTenantRole(String tenantId, Long packageId) {
        // 获取租户套餐
        SysTenantPackage tenantPackage = sysTenantPackageData.findById(packageId);
        if (ObjectUtil.isNull(tenantPackage)) {
            throw new BizException(ErrCode.PACKAGE_NOT_FOUND);
        }
        // 获取套餐菜单id
        List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);

        // 创建角色
        SysRole role = new SysRole();
        role.setTenantId(tenantId);
        role.setRoleName(TenantConstants.TENANT_ADMIN_ROLE_NAME);
        role.setRoleKey(TenantConstants.TENANT_ADMIN_ROLE_KEY);
        role.setRoleSort(1);
        role.setRemark(TenantConstants.TENANT_ADMIN_ROLE_NAME);
        role.setStatus(TenantConstants.NORMAL);
        SysRole retRole=sysRoleData.save(role);
        Long roleId = retRole.getId();

        // 创建角色菜单
        List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
        menuIds.forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        });
        sysRoleMenuData.insertBatch(roleMenus);
        return roleId;
    }

    @Override
    public void updateByBo(SysTenantBo bo) {
        sysTenantData.save(bo.to(SysTenant.class));
    }

    @Override
    public int updateTenantStatus(SysTenantBo bo) {
        SysTenant tenantDataById = sysTenantData.findById(bo.getId());
        tenantDataById.setStatus(bo.getStatus());
        sysTenantData.save(tenantDataById);
        return 0;
    }

    @Override
    public void checkTenantAllowed(String tenantId) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            throw new BizException(ErrCode.UNAUTHORIZED_TENANT);
        }
    }

    @Override
    public void deleteById(Long id) {
        String tenantId=LoginHelper.getTenantId();
        //删除角色
        SysRole querySysRole=new SysRole();
        querySysRole.setTenantId(tenantId);
        List<SysRole> roles =sysRoleData.selectRoleList(querySysRole);
        sysRoleData.deleteByIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
        //删除部门
        //删除系统用户
        //删除字典类型
        //删除字典数据
        //删除系统配置
        sysTenantData.deleteById(id);
    }

    @Override
    public boolean checkCompanyNameUnique(SysTenantBo bo) {
        return sysTenantData.checkCompanyNameUnique(bo.to(SysTenant.class));
    }

    @Override
    public boolean checkAccountBalance(String tenantId) {
        SysTenantVo tenant = this.queryByTenantId(tenantId);
        // 如果余额为-1代表不限制
        if (tenant.getAccountCount() == -1) {
            return true;
        }
        Long userNumber = sysUserData.count();
        // 如果余额大于0代表还有可用名额
        return tenant.getAccountCount() - userNumber > 0;
    }

    @Override
    public boolean checkExpireTime(String tenantId) {
        return false;
    }

    @Override
    public Boolean syncTenantPackage(String tenantId, String packageId) {
        return false;
    }
}
