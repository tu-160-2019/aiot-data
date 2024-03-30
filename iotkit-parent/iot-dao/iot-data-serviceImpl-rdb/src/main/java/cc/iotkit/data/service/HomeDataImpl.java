package cc.iotkit.data.service;

import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.HomeRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IHomeData;
import cc.iotkit.data.model.TbHome;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.space.Home;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cc.iotkit.data.model.QTbHome.tbHome;

@Primary
@Service
@RequiredArgsConstructor
public class HomeDataImpl implements IHomeData, IJPACommData<Home, Long> {

    @Autowired
    private HomeRepository homeRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return homeRepository;
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
        return MapstructUtils.convert(homeRepository.findByUserIdAndCurrent(userId, current), Home.class);
    }

    @Override
    public List<Home> findByUserId(Long userId) {
        return MapstructUtils.convert(homeRepository.findByUserId(userId), Home.class);
    }

    @Override
    public boolean checkHomeNameUnique(Home home) {
        final TbHome ret = jpaQueryFactory.select(tbHome).from(tbHome)
                .where(PredicateBuilder.instance()
                        .and(tbHome.name.eq(home.getName()))
                        .and(tbHome.userId.eq(LoginHelper.getUserId()))
                        .build()).fetchOne();
        return Objects.isNull(ret);
    }


}
