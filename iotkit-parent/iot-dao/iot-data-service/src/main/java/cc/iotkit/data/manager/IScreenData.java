package cc.iotkit.data.manager;

import cc.iotkit.data.IOwnedData;
import cc.iotkit.model.screen.Screen;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:34
 */
public interface IScreenData extends IOwnedData<Screen, Long> {
    Screen findByIsDefault(boolean isDefault);

    List<Screen> findByState(String state);
}
