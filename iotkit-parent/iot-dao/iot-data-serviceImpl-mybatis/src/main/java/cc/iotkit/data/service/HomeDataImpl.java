package cc.iotkit.data.service;

import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.MapstructUtils;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IHomeData;
import cc.iotkit.data.model.TbHome;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.space.Home;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Primary
@Service
@RequiredArgsConstructor
public class HomeDataImpl implements IHomeData, IJPACommData<Home, Long, TbHome> {

    @Autowired
    private HomeService homeService;

    @Override
    public HomeService getBaseRepository() {
        return homeService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbHome.class;
    }

    @Override
    public Class getTClass() {
        return Home.class;
    }

    @Override
    public Home findByUserIdAndCurrent(Long userId, boolean current) {
        return MapstructUtils.convert(homeService.findByUidAndCurrent(userId, current), Home.class);
    }

    @Override
    public List<Home> findByUserId(Long userId) {
        return MapstructUtils.convert(homeService.findByUid(userId), Home.class);
    }

    @Override
    public boolean checkHomeNameUnique(Home home) {
        final TbHome ret = homeService.checkHomeNameUnique(home.getUserId(), home.getName());

        return Objects.isNull(ret);
    }


}
