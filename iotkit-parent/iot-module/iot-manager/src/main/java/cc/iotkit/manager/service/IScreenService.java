package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.manager.dto.bo.screen.DebugChangeBo;
import cc.iotkit.manager.dto.bo.screen.PublishChangeBo;
import cc.iotkit.model.screen.Screen;
import cc.iotkit.model.screen.ScreenApi;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:14
 */
public interface IScreenService {
    Long uploadResourceFile(MultipartFile file, Long id);

    List<ScreenApi> findByScreenId(Long id);

    Screen getDefaultScreen();

    List<ScreenApi> syncResourceApis(Long id);

    void previewApis(List<ScreenApi> screenApis);

    void saveScreenApis(List<ScreenApi> screenApis);

    void debugModeChange(DebugChangeBo debugChange);

    void addBigScreen(Screen screen);

    void saveBigScreen(Screen screen);

    void publishStatusChange(PublishChangeBo data);

    void setDefaultScreen(Long id);

    void deleteScreen(Long id);

    Paging<Screen> getBigScreens(PageRequest<Screen> request);
}
