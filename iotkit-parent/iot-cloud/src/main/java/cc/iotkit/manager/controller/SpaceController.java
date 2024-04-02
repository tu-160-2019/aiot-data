/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.controller;

import cc.iotkit.common.api.Request;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.manager.dto.bo.space.HomeBo;
import cc.iotkit.manager.service.DataOwnerService;
import cc.iotkit.manager.service.IHomeService;
import cc.iotkit.manager.service.ISpaceService;
import cc.iotkit.model.space.Home;
import cc.iotkit.model.space.Space;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"空间"})
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Autowired
    private ISpaceService spaceService;
    @Autowired
    private IHomeService homeService;
    @Autowired
    private DataOwnerService dataOwnerService;

    /**
     * 取用户当前家庭
     */
    @PostMapping("/currentHome")
    public Home getCurrentHome() {
        return homeService.findByUserIdAndCurrent(LoginHelper.getUserId(), true);
    }

    /**
     * 取用户所有家庭
     */
    @PostMapping("/getUserHomes")
    public List<Home> getUserHomes() {
        return homeService.findByUserId(LoginHelper.getUserId());
    }

    /**
     * 切换用户当前家庭
     */
    @PostMapping("/changCurrentHome")
    public void changCurrentHome(@RequestBody @Validated Request<HomeBo> request) {
        HomeBo home=request.getData();
        checkHomeExist(home.getId());
        homeService.changCurrentHome(home);
    }

    /**
     * 添加家庭信息
     */
    @PostMapping("/addHome")
    public void addHome(@RequestBody @Validated Request<HomeBo> request) {
        HomeBo home = request.getData();
        if (!homeService.checkHomeNameUnique(home)) {
            throw new BizException("家庭'" + home.getName() + "'已存在");
        }
        home.setSpaceNum(3);
        home.setUserId(LoginHelper.getUserId());
        Home dbHome = homeService.save(home);
        //添加默认房间
        for (String name : new String[]{"客厅", "卧室", "厨房"}) {
            spaceService.save(Space.builder()
                    .homeId(dbHome.getId())
                    .name(name)
                    .deviceNum(0)
                    .build());
        }
    }

    /**
     * 保存家庭信息
     */
    @PostMapping("/saveHome")
    public void saveHome(@RequestBody @Validated Request<HomeBo> request) {
        HomeBo home=request.getData();
        checkHomeExist(home.getId());
        homeService.save(home);
    }

    /**
     * 删除家庭信息
     */
    @PostMapping("/delHome")
    public void delHome(@RequestBody @Validated Request<Long> request) {
        Long id=request.getData();
        checkHomeExist(id);
        homeService.deleteById(id);
    }

    /**
     * 我的空间列表
     */
    @PostMapping("/getSpaces")
    public List<Space> getSpaces(@RequestBody @Validated Request<Long> request) {
        return spaceService.findByHomeId(request.getData());
    }

    /**
     * 在当前家庭中添加空间
     */
    @PostMapping("/addSpace")
    public void addSpace(@RequestBody @Validated Request<Space> request) {
        Long userId=LoginHelper.getUserId();
        Home currHome = homeService.findByUserIdAndCurrent(userId, true);
        if (currHome == null) {
            throw new BizException(ErrCode.CURRENT_HOME_NOT_FOUND);
        }
        spaceService.save(Space.builder()
                .homeId(currHome.getId())
                .name(request.getData().getName())
                .deviceNum(0)
                .build());
    }

    @PostMapping("/delSpace")
    public void delSpace(@RequestBody @Validated Request<Long> request) {
        Long spaceId=request.getData();
        checkExist(spaceId);
        spaceService.deleteById(spaceId);
    }

    @PostMapping("/saveSpace")
    public void saveSpace(@RequestBody @Validated Request<Space> request) {
        Space space=request.getData();
        Space oldSpace = checkExist(space.getId());
        oldSpace.setName(space.getName());
        spaceService.save(oldSpace);
    }

    private Space checkExist(Long id) {
        Space space = spaceService.findById(id);
        if (space == null) {
            throw new BizException(ErrCode.SPACE_NOT_FOUND);
        }
        return space;
    }

    private Home checkHomeExist(Long id) {
        Home home = homeService.findById(id);
        if (home == null) {
            throw new BizException(ErrCode.HOME_NOT_FOUND);
        }
        return home;
    }

}
