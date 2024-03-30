package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysDeptBo;
import cc.iotkit.system.dto.vo.SysDeptVo;
import cc.iotkit.system.service.ISysDeptService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    private final ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @SaCheckPermission("system:dept:list")
    @ApiOperation("获取部门列表")
    @PostMapping("/list")
    public List<SysDeptVo> list(@RequestBody @Validated PageRequest<SysDeptBo> dept) {
        return deptService.selectDeptList(dept.getData());
    }

    /**
     * 查询部门列表（排除节点）
     */
    @ApiOperation("查询部门列表（排除节点）")
    @SaCheckPermission("system:dept:list")
    @PostMapping("/list/exclude")
    public List<SysDeptVo> excludeChild(@Validated @RequestBody Request<Long> request) {
        Long deptId = request.getData();
        List<SysDeptVo> depts = deptService.selectDeptList(new SysDeptBo());
        depts.removeIf(d -> d.getId().equals(deptId)
                || StringUtils.splitList(d.getAncestors()).contains(Convert.toStr(deptId)));
        return depts;
    }

    /**
     * 根据部门编号获取详细信息
     */
    @SaCheckPermission("system:dept:query")
    @ApiOperation("根据部门编号获取详细信息")
    @PostMapping(value = "/getInfo")
    public SysDeptVo getInfo(@Validated @RequestBody Request<Long> bo) {
        Long deptId = bo.getData();
        deptService.checkDeptDataScope(deptId);
        return deptService.selectDeptById(deptId);
    }

    /**
     * 新增部门
     */
    @SaCheckPermission("system:dept:add")
    @ApiOperation("新增部门")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated @RequestBody Request<SysDeptBo> bo) {
        SysDeptBo dept = bo.getData();
        if (!deptService.checkDeptNameUnique(dept)) {
            fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        deptService.insertDept(dept);
    }

    /**
     * 修改部门
     */
    @ApiOperation("修改部门")
    @SaCheckPermission("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated @RequestBody Request<SysDeptBo> bo) {
        SysDeptBo dept = bo.getData();
        Long deptId = dept.getId();
        deptService.checkDeptDataScope(deptId);
        if (dept.getParentId().equals(deptId)) {
            fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            fail("该部门包含未停用的子部门！");
        }
        deptService.updateDept(dept);
    }

    /**
     * 删除部门
     */
    @SaCheckPermission("system:dept:remove")
    @ApiOperation("删除部门")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<Long> bo) {
        Long deptId = bo.getData();
        if (deptService.hasChildByDeptId(deptId)) {
            warn("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            warn("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        deptService.deleteDeptById(deptId);
    }
}
