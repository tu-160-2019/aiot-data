package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IScreenData;
import cc.iotkit.data.model.TbScreen;
import cc.iotkit.model.screen.Screen;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:21
 */
@Primary
@Service
@RequiredArgsConstructor
public class ScreenDataImpl implements IScreenData,IJPACommData<Screen,Long> {
//public class ScreenDataImpl implements IScreenData,IJPACommData<Screen,Long, TbScreen> {

    @Resource
    @Qualifier("DBScreenServiceImpl")
    private ScreenService screenService;

    @Override
    public ScreenService getBaseRepository() {
        return screenService;
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
        return screenService.findByIsDefault(isDefault);
    }

    @Override
    public List<Screen> findByState(String state) {
        return screenService.findByState(state);
    }
}
