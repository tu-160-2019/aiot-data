package cc.iotkit.data.service;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.model.TbSysDept;
import cc.iotkit.data.system.ISysDeptData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysDept;
import cn.hutool.core.util.ObjectUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysDeptDataImpl implements ISysDeptData, IJPACommData<SysDept, Long, TbSysDept> {

    @Resource
    @Qualifier("DBSysDeptServiceImpl")
    private SysDeptService deptService;

    @Override
    public SysDeptService getBaseRepository() {
        return deptService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysDept.class;
    }

    @Override
    public Class getTClass() {
        return SysDept.class;
    }


    @Override
    public List<SysDept> findDepts(SysDept dept) {
        LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper = new LambdaQueryWrapper<TbSysDept>()
                .eq(ObjectUtil.isNotNull(dept.getId()), TbSysDept::getId, dept.getId())
                .eq(ObjectUtil.isNotNull(dept.getParentId()), TbSysDept::getParentId, dept.getParentId())
                .eq(ObjectUtil.isNotNull(dept.getStatus()), TbSysDept::getStatus, dept.getStatus())
                .like(ObjectUtil.isNotNull(dept.getDeptName()), TbSysDept::getDeptName, dept.getDeptName());
        List<TbSysDept> sysDeptList = deptService.findByConditions(lambdaQueryWrapper);
        return MapstructUtils.convert(sysDeptList, SysDept.class);
    }

    @Override
    public List<SysDept> findByRoleId(Long roleId) {
        List<TbSysDept> list = deptService.findByRoleId(roleId);
        return MapstructUtils.convert(list, SysDept.class);

    }

    @Override
    public long countByParentId(Long parentId) {
        return deptService.countByParentId(parentId);
    }

    @Override
    public List<SysDept> findByDeptId(Long deptId) {
        List<TbSysDept> sysDeptList = deptService.findByConditions(new LambdaQueryWrapper<>()).stream()
                .filter(o -> o.getAncestors() != null && o.getAncestors().contains(deptId.toString()))
                .collect(Collectors.toList());
        return MapstructUtils.convert(sysDeptList, SysDept.class);
    }

    @Override
    public boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId) {
        LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper = new LambdaQueryWrapper<TbSysDept>()
                .eq(TbSysDept::getDeptName, deptName)
                .eq(TbSysDept::getParentId, parentId);
        if (ObjectUtil.isNotNull(deptId)) {
            lambdaQueryWrapper.ne(TbSysDept::getId, deptId);
        }
        return deptService.count(lambdaQueryWrapper) == 0;
    }

    @Override
    public long selectNormalChildrenDeptById(Long deptId) {
        List<TbSysDept> sysDeptList = deptService.findByConditions(new LambdaQueryWrapper<TbSysDept>()
                .eq(TbSysDept::getStatus, UserConstants.DEPT_NORMAL));
        return sysDeptList.stream()
                .map(TbSysDept::getAncestors).filter(e -> e.contains(deptId.toString())).count();
    }
}
