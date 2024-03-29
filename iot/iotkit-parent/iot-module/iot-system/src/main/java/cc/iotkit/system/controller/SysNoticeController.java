package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysNoticeBo;
import cc.iotkit.system.dto.vo.SysNoticeVo;
import cc.iotkit.system.service.ISysNoticeService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    private final ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @SaCheckPermission("system:notice:list")
    @ApiOperation(value = "获取通知公告列表", notes = "获取通知公告列表")
    @PostMapping("/list")
    public Paging<SysNoticeVo> list(@Validated @RequestBody PageRequest<SysNoticeBo> query) {
        return noticeService.selectPageNoticeList( query);
    }

    /**
     * 根据通知公告编号获取详细信息
     *
     */
    @ApiOperation(value = "根据通知公告编号获取详细信息", notes = "根据通知公告编号获取详细信息")
    @SaCheckPermission("system:notice:query")
    @PostMapping(value = "/getDetail")
    public SysNoticeVo getInfo(@Validated @RequestBody Request<Long> bo) {
        return noticeService.selectNoticeById(bo.getData());
    }

    /**
     * 新增通知公告
     */
    @ApiOperation(value = "新增通知公告", notes = "新增通知公告")
    @SaCheckPermission("system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated @RequestBody Request<SysNoticeBo> bo) {
        noticeService.insertNotice(bo.getData());
    }

    /**
     * 修改通知公告
     */
    @SaCheckPermission("system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated @RequestBody Request<SysNoticeBo> bo) {
        noticeService.updateNotice(bo.getData());
    }

    /**
     * 删除通知公告
     *
     */
    @ApiOperation(value = "删除通知公告", notes = "删除通知公告")
    @SaCheckPermission("system:notice:remove")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody  Request<List<Long>> bo) {
        noticeService.deleteNoticeByIds(bo.getData());
    }
}
