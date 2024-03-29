package cc.iotkit.system.service.impl;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.service.DeptService;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.SpringUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.utils.TreeBuildUtils;
import cc.iotkit.data.system.ISysDeptData;
import cc.iotkit.data.system.ISysRoleData;
import cc.iotkit.data.system.ISysUserData;
import cc.iotkit.model.system.SysDept;
import cc.iotkit.system.dto.bo.SysDeptBo;
import cc.iotkit.system.dto.vo.SysDeptVo;
import cc.iotkit.system.service.ISysDeptService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl implements ISysDeptService, DeptService {

    private final ISysDeptData sysDeptData;

    private final ISysRoleData sysRoleData;

    private final ISysUserData sysUserData;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptVo> selectDeptList(SysDeptBo dept) {
        return MapstructUtils.convert(
                sysDeptData.findDepts(dept.to(SysDept.class))
                , SysDeptVo.class);
    }

    /**
     * 查询部门树结构信息
     *
     * @param bo 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<Tree<Long>> selectDeptTreeList(SysDeptBo bo) {
        List<SysDept> depts = sysDeptData.findDepts(bo.to(SysDept.class));
        return buildDeptTreeSelect(depts);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<Long>> buildDeptTreeSelect(List<SysDept> depts) {
        if (CollUtil.isEmpty(depts)) {
            return CollUtil.newArrayList();
        }
        return TreeBuildUtils.build(depts, (dept, tree) ->
                tree.setId(dept.getId())
                        .setParentId(dept.getParentId())
                        .setName(dept.getDeptName())
                        .setWeight(dept.getOrderNum()));
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        return sysDeptData.findByRoleId(roleId).stream()
                .map(SysDept::getId).collect(Collectors.toList());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
//    @Cacheable(cacheNames = CacheNames.SYS_DEPT, key = "#deptId")
    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        SysDept dept = sysDeptData.findById(deptId);
        if (ObjectUtil.isNull(dept)) {
            return null;
        }

        SysDept parentDept = sysDeptData.findById(dept.getParentId());
        dept.setParentName(ObjectUtil.isNotNull(parentDept) ? parentDept.getDeptName() : null);
        return MapstructUtils.convert(dept, SysDeptVo.class);
    }

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    @Override
    public String selectDeptNameByIds(String deptIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(deptIds, Convert::toLong)) {
            SysDeptVo vo = SpringUtils.getAopProxy(this).selectDeptById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(vo.getDeptName());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public long selectNormalChildrenDeptById(Long deptId) {
        return sysDeptData.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        return sysDeptData.countByParentId(deptId) > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return sysUserData.countByDeptId(deptId) > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptBo dept) {
        return sysDeptData.checkDeptNameUnique(dept.getDeptName(), dept.getParentId(), dept.getId());
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (ObjectUtil.isNull(deptId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        SysDept dept = sysDeptData.findById(deptId);
        if (ObjectUtil.isNull(dept)) {
            throw new BizException("没有权限访问部门数据！");
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     */
    @Override
    public void insertDept(SysDeptBo bo) {
        SysDept parent = sysDeptData.findById(bo.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(parent.getStatus())) {
            throw new BizException("部门停用，不允许新增");
        }
        SysDept dept = MapstructUtils.convert(bo, SysDept.class);
        dept.setAncestors(parent.getAncestors() + StringUtils.SEPARATOR + dept.getParentId());
        sysDeptData.save(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
//    @CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#bo.deptId")
    @Override
    public void updateDept(SysDeptBo bo) {
        SysDept dept = MapstructUtils.convert(bo, SysDept.class);
        SysDept oldDept = sysDeptData.findById(bo.getId());
        if (oldDept == null) {
            throw new BizException(ErrCode.DATA_NOT_EXIST);
        }

        if (!oldDept.getParentId().equals(dept.getParentId())) {
            // 如果是新父部门 则校验是否具有新父部门权限 避免越权
            this.checkDeptDataScope(dept.getParentId());
            SysDept newParentDept = sysDeptData.findById(dept.getParentId());
            if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
                String newAncestors = newParentDept.getAncestors() + StringUtils.SEPARATOR + newParentDept.getId();
                String oldAncestors = oldDept.getAncestors();
                dept.setAncestors(newAncestors);
                updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
            }
        }

        sysDeptData.save(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals(UserConstants.DEPT_NORMAL, dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
//        String ancestors = dept.getAncestors();
//        Long[] deptIds = Convert.toLongArray(ancestors);
//        baseMapper.update(null, new LambdaUpdateWrapper<SysDept>()
//                .set(SysDept::getStatus, UserConstants.DEPT_NORMAL)
//                .in(SysDept::getDeptId, Arrays.asList(deptIds)));
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
//        List<SysDept> children = baseMapper.selectList(new LambdaQueryWrapper<SysDept>()
//                .apply(DataBaseHelper.findInSet(deptId, "ancestors")));
//        List<SysDept> list = new ArrayList<>();
//        for (SysDept child : children) {
//            SysDept dept = new SysDept();
//            dept.setDeptId(child.getDeptId());
//            dept.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
//            list.add(dept);
//        }
//        if (CollUtil.isNotEmpty(list)) {
//            if (baseMapper.updateBatchById(list)) {
//                list.forEach(dept -> CacheUtils.evict(CacheNames.SYS_DEPT, dept.getDeptId()));
//            }
//        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
//    @CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#deptId")
    @Override
    public void deleteDeptById(Long deptId) {
        sysDeptData.deleteById(deptId);
    }

}
