package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.ScreenRepository;
import cc.iotkit.data.manager.IScreenData;
import cc.iotkit.data.model.TbScreen;
import cc.iotkit.model.screen.Screen;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:21
 */
@Primary
@Service
@RequiredArgsConstructor
public class ScreenDataImpl implements IScreenData,IJPACommData<Screen,Long> {

    @Autowired
    private ScreenRepository screenRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return screenRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbScreen.class;
    }

    @Override
    public Class getTClass() {
        return Screen.class;
    }

    @Override
    public Screen findByIsDefault(boolean isDefault) {
        return MapstructUtils.convert(screenRepository.findByIsDefault(isDefault),Screen.class);
    }

    @Override
    public List<Screen> findByState(String state) {
        return MapstructUtils.convert(screenRepository.findByState(state),Screen.class);
    }
}
