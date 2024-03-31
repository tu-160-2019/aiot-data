package cc.iotkit.data.service.impl;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.ScreenMapper;
import cc.iotkit.data.model.TbScreen;
import cc.iotkit.data.service.ScreenService;
import cc.iotkit.model.screen.Screen;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBScreenServiceImpl")
@Primary
public class ScreenServiceImpl extends ServiceImpl<ScreenMapper, TbScreen> implements ScreenService {
    @Override
    public Screen findByIsDefault(boolean isDefault) {
        TbScreen tbScreen = this.baseMapper.selectOne(new LambdaQueryWrapper<TbScreen>()
                .eq(TbScreen::getIsDefault, isDefault));
        return MapstructUtils.convert(tbScreen, Screen.class);
    }

    @Override
    public List<Screen> findByState(String state) {
        List<TbScreen> tbScreenList = this.baseMapper.selectList(new LambdaQueryWrapper<TbScreen>()
                .eq(TbScreen::getState, state));
        return MapstructUtils.convert(tbScreenList, Screen.class);
    }
}

