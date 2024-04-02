package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.manager.dto.bo.channel.ChannelConfigBo;
import cc.iotkit.manager.dto.bo.channel.ChannelTemplateBo;
import cc.iotkit.manager.dto.vo.channel.ChannelConfigVo;
import cc.iotkit.manager.dto.vo.channel.ChannelTemplateVo;
import cc.iotkit.manager.service.NotifyService;
import cc.iotkit.model.notify.Channel;
import cc.iotkit.model.notify.ChannelConfig;
import cc.iotkit.model.notify.ChannelTemplate;
import cc.iotkit.model.notify.NotifyMessage;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * author: 石恒
 * date: 2023-05-11 15:17
 * description:
 **/
@Api(tags = {"消息通知"})
@Slf4j
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Resource
    private NotifyService notifyService;

    @ApiOperation("获取通道类型列表")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/getList")
    public List<Channel> getChannelList() {
        return notifyService.getChannelList();
    }

    @ApiOperation("获取通道配置分页列表")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/config/getList")
    public Paging<ChannelConfigVo> getChannelConfigList(@RequestBody @Validated(QueryGroup.class) PageRequest<ChannelConfigBo> request) {
        return notifyService.getChannelConfigList(request);
    }

    @ApiOperation("获取通道配置列表")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/config/getAll")
    public List<ChannelConfigVo> getChannelConfigAll() {
        return notifyService.getChannelConfigAll();
    }

    @ApiOperation("新增通道配置")
    @SaCheckPermission("iot:channel:add")
    @PostMapping("/channel/config/add")
    public ChannelConfig addChannelConfig(@RequestBody @Validated(EditGroup.class) Request<ChannelConfig> request) {
        return notifyService.addChannelConfig(request.getData());
    }

    @ApiOperation("根据ID获取通道配置")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/config/getById")
    public ChannelConfig getChannelConfigById(@RequestBody @Validated(QueryGroup.class) Request<Long> request) {
        return notifyService.getChannelConfigById(request.getData());
    }

    @ApiOperation("修改通道配置")
    @SaCheckPermission("iot:channel:edit")
    @PostMapping("/channel/config/updateById")
    public ChannelConfig updateChannelConfigById(@RequestBody @Validated Request<ChannelConfig> request) {
        return notifyService.updateChannelConfigById(request.getData());
    }

    @ApiOperation("删除通道配置")
    @SaCheckPermission("iot:channel:remove")
    @PostMapping("/channel/config/delById")
    public Boolean delChannelConfigById(@RequestBody @Validated Request<Long> request) {
        return notifyService.delChannelConfigById(request.getData());
    }

    @ApiOperation("获取通道模板列表")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/template/getList")
    public Paging<ChannelTemplateVo> getChannelTemplateList(@RequestBody @Validated(QueryGroup.class) PageRequest<ChannelTemplateBo> request) {
        return notifyService.getChannelTemplateList(request);
    }

    @ApiOperation("新增通道模板")
    @SaCheckPermission("iot:channel:add")
    @PostMapping("/channel/template/add")
    public ChannelTemplate addChannelTemplate(@RequestBody @Validated(EditGroup.class) Request<ChannelTemplateBo> request) {
        return notifyService.addChannelTemplate(request.getData());
    }

    @ApiOperation("根据ID获取通道模板")
    @SaCheckPermission("iot:channel:query")
    @PostMapping("/channel/template/getById")
    public ChannelTemplate getChannelTemplateById(@RequestBody @Validated Request<Long> request) {
        return notifyService.getChannelTemplateById(request.getData());
    }

    @ApiOperation("修改通道模板")
    @SaCheckPermission("iot:channel:edit")
    @PostMapping("/channel/template/updateById")
    public ChannelTemplate updateChannelTemplateById(@RequestBody @Validated Request<ChannelTemplate> request) {
        return notifyService.updateChannelTemplateById(request.getData());
    }

    @ApiOperation("删除通道模板")
    @SaCheckPermission("iot:channel:remove")
    @PostMapping("/channel/template/delById")
    public Boolean delChannelTemplateById(@RequestBody @Validated(EditGroup.class) Request<Long> request) {
        return notifyService.delChannelTemplateById(request.getData());
    }

    @ApiOperation("消息列表")
    @SaCheckPermission("iot:channelMsg:query")
    @PostMapping("/message/getList")
    public Paging<NotifyMessage> messageList(@RequestBody @Validated(QueryGroup.class) PageRequest<NotifyMessage> request) {
        return notifyService.getNotifyMessageList(request);
    }

}
