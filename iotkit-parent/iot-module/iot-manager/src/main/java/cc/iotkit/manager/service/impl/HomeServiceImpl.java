package cc.iotkit.manager.service.impl;

import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.data.manager.IHomeData;
import cc.iotkit.data.manager.ISpaceData;
import cc.iotkit.data.manager.ISpaceDeviceData;
import cc.iotkit.manager.dto.bo.space.HomeBo;
import cc.iotkit.manager.service.IHomeService;
import cc.iotkit.model.space.Home;
import cc.iotkit.model.space.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 10:21
 */
@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private IHomeData homeData;

    @Autowired
    private ISpaceData spaceData;

    @Autowired
    private ISpaceDeviceData spaceDeviceData;

    @Override
    public Home save(HomeBo home) {
        return homeData.save(home.to(Home.class));
    }

    @Override
    public Home findByUserIdAndCurrent(Long userId, boolean current) {
        return homeData.findByUserIdAndCurrent(userId,current);
    }

    @Override
    public List<Home> findByUserId(Long userId) {
        return homeData.findByUserId(userId);
    }

    @Override
    public Home findById(Long id) {
        return homeData.findById(id);
    }

    @Override
    public boolean checkHomeNameUnique(HomeBo home) {
        return homeData.checkHomeNameUnique(home.to(Home.class));
    }

    @Override
    public void deleteById(Long id) {
        //先删除空间设备，再删除空间，再删除家庭
        List<Space> spaces=spaceData.findByHomeId(id);
        for(Space space:spaces){
            spaceDeviceData.deleteAllBySpaceId(space.getId());
            spaceData.deleteById(space.getId());
        }
        homeData.deleteById(id);
    }

    @Override
    public void changCurrentHome(HomeBo home) {
        Home oldHome=homeData.findByUserIdAndCurrent(LoginHelper.getUserId(), true);
        oldHome.setCurrent(false);
        homeData.save(oldHome);
        Home newHome=homeData.findById(home.getId());
        newHome.setCurrent(true);
        homeData.save(newHome);
    }
}
