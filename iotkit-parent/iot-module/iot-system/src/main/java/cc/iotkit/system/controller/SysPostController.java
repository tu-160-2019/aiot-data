package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysPostBo;
import cc.iotkit.system.dto.vo.SysPostVo;
import cc.iotkit.system.service.ISysPostService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 岗位信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/post")
@Api(tags = "岗位信息")
public class SysPostController extends BaseController {

    private final ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @ApiOperation("获取岗位列表")
    @SaCheckPermission("system:post:list")
    @PostMapping("/list")
    public Paging<SysPostVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<SysPostBo> query) {
        return postService.selectPagePostList(query);
    }

    /**
     * 导出岗位列表
     */
    @ApiOperation("导出岗位列表")
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:post:export")
    @PostMapping("/export")
    public void export(@Validated(QueryGroup.class) SysPostBo post, HttpServletResponse response) {
        List<SysPostVo> list = postService.selectPostList(post);
        ExcelUtil.exportExcel(list, "岗位数据", SysPostVo.class, response);
    }

    /**
     * 根据岗位编号获取详细信息
     *
     * @param postId 岗位ID
     */
    @ApiOperation("根据岗位编号获取详细信息")
    @SaCheckPermission("system:post:query")
    @PostMapping(value = "/getInfo")
    public SysPostVo getInfo(@RequestBody @Validated Request<Long> postId) {
        return postService.selectPostById(postId.getData());
    }

    /**
     * 新增岗位
     */
    @ApiOperation("新增岗位")
    @SaCheckPermission("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@RequestBody @Validated(EditGroup.class) Request<SysPostBo> post) {
        if (!postService.checkPostNameUnique(post.getData())) {
            fail("新增岗位'" + post.getData().getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post.getData())) {
            fail("新增岗位'" + post.getData().getPostName() + "'失败，岗位编码已存在");
        }
        postService.insertPost(post.getData());
    }

    /**
     * 修改岗位
     */
    @ApiOperation("修改岗位")
    @SaCheckPermission("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@RequestBody @Validated(EditGroup.class) Request<SysPostBo> post) {
        postService.updatePost(post.getData());
    }

    /**
     * 删除岗位
     *
     * @param postIds 岗位ID串
     */
    @ApiOperation("删除岗位")
    @SaCheckPermission("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@RequestBody @Validated Request<List<Long>> postIds) {
        postService.deletePostByIds(postIds.getData());
    }

    /**
     * 获取岗位选择框列表
     */
    @ApiOperation("获取岗位选择框列表")
    @PostMapping("/optionselect")
    public List<SysPostVo> optionselect() {
        return postService.selectPostAll();
    }
}
