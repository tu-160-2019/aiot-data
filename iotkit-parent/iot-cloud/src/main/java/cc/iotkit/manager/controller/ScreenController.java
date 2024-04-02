package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.manager.dto.bo.screen.DebugChangeBo;
import cc.iotkit.manager.dto.bo.screen.PublishChangeBo;
import cc.iotkit.manager.service.IScreenService;
import cc.iotkit.model.screen.Screen;
import cc.iotkit.model.screen.ScreenApi;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 14:56
 */
@Api(tags = {"大屏接口"})
@Slf4j
@RestController
@RequestMapping("/screen")
public class ScreenController {

    @Autowired
    private IScreenService screenService;

    @ApiOperation(value = "获取大屏列表", httpMethod = "POST")
    @SaCheckPermission("iot:screen:list")
    @PostMapping("/getScreens")
    public Paging<Screen> getBigScreens(@Validated @RequestBody PageRequest<Screen> request) {
        return screenService.getBigScreens(request);
    }

    @ApiOperation(value = "上传大屏资源包")
    @SaCheckPermission("iot:screen:add")
    @PostMapping("/uploadResourceFile")
    public Long uploadResourceFile(@RequestParam("file") MultipartFile file,
                                   @RequestBody @Validated Request<Long> id){
        if (file == null) {
            throw new BizException(ErrCode.PARAMS_EXCEPTION);
        }
        log.info("saving upload resource file:{}", file.getName());
        return screenService.uploadResourceFile(file,id.getData());
    }

    @ApiOperation(value = "获取大屏接口")
    @SaCheckPermission("iot:screen:list")
    @PostMapping("/getScreenApis")
    public List<ScreenApi> getScreenApis(@RequestBody @Validated Request<Long> id) {
        if (ObjectUtil.isEmpty(id.getData())) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        return screenService.findByScreenId(id.getData());
    }

    @ApiOperation(value = "获取默认大屏")
    @SaCheckPermission("iot:screen:query")
    @PostMapping("/getDefaultScreen")
    public Screen getDefaultScreen() {
        return screenService.getDefaultScreen();
    }

    @ApiOperation(value = "同步资源包接口")
    @SaCheckPermission("iot:screen:query")
    @PostMapping("/syncResourceApis")
    public List<ScreenApi> syncResourceApis(@RequestBody @Validated Request<Long> id) {
        if (ObjectUtil.isEmpty(id.getData())) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        return screenService.syncResourceApis(id.getData());
    }

    @ApiOperation(value = "预览接口")
    @SaCheckPermission("iot:screen:query")
    @PostMapping("/previewApis")
    public void previewApis(@RequestBody  @Validated Request<List<ScreenApi>> screenApis) {
        if (ObjectUtil.isNull(screenApis.getData()) || screenApis.getData().isEmpty()) {
            throw new BizException(ErrCode.API_LIST_BLANK);
        }
        screenService.previewApis(screenApis.getData());
    }

    @ApiOperation(value = "保存大屏接口")
    @SaCheckPermission("iot:screen:edit")
    @PostMapping("/saveScreenApis")
    public void saveScreenApis(@RequestBody @Validated Request<List<ScreenApi>> screenApis) {
        if (ObjectUtil.isNull(screenApis.getData()) || screenApis.getData().isEmpty()) {
            throw new BizException(ErrCode.API_LIST_BLANK);
        }
        screenService.saveScreenApis(screenApis.getData());
    }

    @ApiOperation(value = "调试模式转换")
    @SaCheckPermission("iot:screen:edit")
    @PostMapping("/debugModeChange")
    public void debugMode(@RequestBody @Validated Request<DebugChangeBo> debugChange) {
        screenService.debugModeChange(debugChange.getData());
    }

    @ApiOperation(value = "添加大屏")
    @SaCheckPermission("iot:screen:add")
    @PostMapping("/addScreen")
    public void addScreen(@RequestBody @Validated Request<Screen> screen) {
        screenService.addBigScreen(screen.getData());
    }

    @ApiOperation(value = "保存大屏")
    @SaCheckPermission("iot:screen:edit")
    @PostMapping("/saveScreen")
    public void saveScreen(@RequestBody @Validated Request<Screen> screen) {
        screenService.saveBigScreen(screen.getData());
    }

    @ApiOperation(value = "发布状态改变")
    @SaCheckPermission("iot:screen:edit")
    @PostMapping("/publishStatusChange")
    public void publishStatusChange(@RequestBody @Validated Request<PublishChangeBo> req) {
        screenService.publishStatusChange(req.getData());
    }

    @ApiOperation(value = "设置默认大屏")
    @SaCheckPermission("iot:screen:edit")
    @PostMapping("/setDefaultScreen")
    public void setDefaultScreen(@RequestBody @Validated Request<Long> id) {
        if (ObjectUtil.isEmpty(id.getData())) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        screenService.setDefaultScreen(id.getData());
    }

    @ApiOperation(value = "删除大屏", httpMethod = "POST")
    @SaCheckPermission("iot:screen:remove")
    @PostMapping("/deleteScreen")
    public void deleteScreen(@RequestBody @Validated Request<Long> id) {
        if (ObjectUtil.isEmpty(id.getData())) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        screenService.deleteScreen(id.getData());
    }
}
