package cc.iotkit.screen;

import cc.iotkit.data.manager.IScreenApiData;
import cc.iotkit.data.manager.IScreenData;
import cc.iotkit.model.screen.Screen;
import cc.iotkit.model.screen.ScreenApi;
import cc.iotkit.screen.api.ScreenApiHandle;
import cc.iotkit.screen.config.ScreenConfig;
import cc.iotkit.screen.staticres.ScreenComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：tfd
 * @Date：2023/6/25 17:03
 */
@Slf4j
@Component
public class ScreenManager {

    @Autowired
    private IScreenApiData screenApiData;

    @Autowired
    private IScreenData screenData;

    @Autowired
    private ScreenConfig screenConfig;

    private final Map<Long, ScreenComponent> screens = new HashMap<>();
    private final Map<Long, Boolean> states = new HashMap<>();

    @PostConstruct
    public void init() {
        List<Screen> screenList = screenData.findByState(
                Screen.STATE_RUNNING);
        for (Screen screen : screenList) {
            try {
                register(screen);
                publish(screen);
            } catch (Throwable e) {
                log.error("init screen error", e);
            }
        }
    }

    public void register(Screen screen) {
        Long id = screen.getId();
        if (screens.containsKey(id)) {
            return;
        }
        ScreenComponent screenComponent=new ScreenComponent();
        String[] pathNames=screen.getResourceFile().split("\\.");
        screenComponent.create(screen.getPort(),pathNames.length>1?pathNames[0]:"",screenConfig);
        screens.put(id,screenComponent);
    }

    public void publish(Screen screen) {
        Long id = screen.getId();
        ScreenComponent screenComponent = screens.get(id);
        if (screenComponent == null) {
            return;
        }
        ScreenApiHandle screenApiHandle=new ScreenApiHandle(id,screenApiData.findByScreenId(id));
        screenComponent.setApiHandle(screenApiHandle);
        screenComponent.publish();
        states.put(id, true);
    }

    public void unPublish(Screen screen) {
        Long id = screen.getId();
        ScreenComponent screenComponent = screens.get(id);
        if (screenComponent == null) {
            return;
        }
        screens.remove(id);
        states.remove(id);
        screenComponent.unPublish();
    }

    public void previewApis(Screen screen,List<ScreenApi> screenApis) {
        Long id = screen.getId();
        ScreenComponent screenComponent = screens.get(id);
        if (screenComponent == null) {
            return;
        }
        screenComponent.previewApis(screenApis);
    }

    public List<ScreenApi> getScreenApis(Screen screen) {
        Long id = screen.getId();
        ScreenComponent screenComponent = screens.get(id);
        if (screenComponent == null) {
            return Collections.emptyList();
        }
        return screenComponent.getScreenApis();
    }

    public void debugMode(Screen screen,boolean state) {
        Long id = screen.getId();
        ScreenComponent screenComponent = screens.get(id);
        if (screenComponent == null) {
            return;
        }
        screenComponent.debugMode(state);
    }

    public boolean isRunning(String id) {
        return states.containsKey(id) && states.get(id);
    }
}
