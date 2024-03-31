package cc.iotkit.data.service;

import cc.iotkit.data.model.TbScreen;
import cc.iotkit.model.screen.Screen;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ScreenService extends IService<TbScreen> {
    Screen findByIsDefault(boolean isDefault);

    List<Screen> findByState(String state);
}
