package cc.iotkit.manager.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.ReflectUtil;
import cc.iotkit.data.manager.IScreenApiData;
import cc.iotkit.data.manager.IScreenData;
import cc.iotkit.manager.dto.bo.screen.DebugChangeBo;
import cc.iotkit.manager.dto.bo.screen.PublishChangeBo;
import cc.iotkit.manager.service.DataOwnerService;
import cc.iotkit.manager.service.IScreenService;
import cc.iotkit.model.screen.Screen;
import cc.iotkit.model.screen.ScreenApi;
import cc.iotkit.screen.ScreenManager;
import cc.iotkit.screen.config.ScreenConfig;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:15
 */
@Service
@Slf4j
public class ScreenServiceImpl implements IScreenService {
    @Autowired
    private IScreenData screenData;
    @Autowired
    private IScreenApiData screenApiData;
    @Autowired
    private DataOwnerService dataOwnerService;
    @Autowired
    private ScreenConfig screenConfig;
    @Autowired
    private ScreenManager screenManager;

    @Override
    public Long uploadResourceFile(MultipartFile file, Long id) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (ObjectUtil.isNotNull(id)) {
                getAndCheckBigScreen(id);
            } else {
                id = YitIdHelper.nextId();
            }
            Path filePath = screenConfig.getBigScreenFilePath(String.valueOf(id));
            Files.createDirectories(filePath);
            Path targetLocation = filePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            ZipUtil.unzip(filePath.toString()+"/"+fileName);
            return id;
        } catch (IOException ex) {
            throw new BizException(ErrCode.UPLOAD_FILE_ERROR, ex);
        }
    }

    @Override
    public List<ScreenApi> findByScreenId(Long id) {
        return screenApiData.findByScreenId(id);
    }

    @Override
    public Screen getDefaultScreen() {
        return screenData.findByIsDefault(true);
    }

    @Override
    public List<ScreenApi> syncResourceApis(Long id) {
        Screen screen = getAndCheckBigScreen(id);
        return screenManager.getScreenApis(screen);
    }

    @Override
    public void previewApis(List<ScreenApi> screenApis) {
        Screen screen = getAndCheckBigScreen(screenApis.get(0).getScreenId());
        screenManager.previewApis(screen,screenApis);
    }

    @Override
    public void saveScreenApis(List<ScreenApi> screenApis) {
        Screen screen = getAndCheckBigScreen(screenApis.get(0).getScreenId());
        screenApiData.deleteByScreenId(screen.getId());
        screenApiData.batchSave(screenApis);
    }

    @Override
    public void debugModeChange(DebugChangeBo debugChange) {
        Screen screen = getAndCheckBigScreen(debugChange.getId());
        screenManager.debugMode(screen,debugChange.getState());
    }

    @Override
    public void addBigScreen(Screen screen) {
        String id = String.valueOf(screen.getId());
        if (!StringUtils.hasLength(id)) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        Path resPath = screenConfig.getBigScreenFilePath(id);
        if (!resPath.resolve(screen.getResourceFile()).toFile().exists()) {
            throw new BizException(ErrCode.RESOURCE_FILE_NOT_FOUND);
        }
        Screen s = screenData.findById(screen.getId());
        if (s != null) {
            throw new BizException(ErrCode.BIG_SCREEN_ALREADY);
        }
        try {
            screen.setCreateAt(System.currentTimeMillis());
            screen.setIsDefault(false);
            screenData.save(screen);
        } catch (Throwable e) {
            throw new BizException(ErrCode.ADD_BIG_SCREEN_ERROR, e);
        }
    }

    @Override
    public void saveBigScreen(Screen screen) {
        String id = String.valueOf(screen.getId());
        if (!StringUtils.hasLength(id)) {
            throw new BizException(ErrCode.ID_BLANK);
        }
        Path jarPath = screenConfig.getBigScreenFilePath(id);
        if (!jarPath.resolve(screen.getResourceFile()).toFile().exists()) {
            throw new BizException(ErrCode.RESOURCE_FILE_NOT_FOUND);
        }
        Screen oldScreen = getAndCheckBigScreen(screen.getId());
        screen = ReflectUtil.copyNoNulls(screen, oldScreen);
        try {
            screenData.save(screen);
        } catch (Throwable e) {
            throw new BizException(ErrCode.ADD_BIG_SCREEN_ERROR, e);
        }
    }

    @Override
    public void publishStatusChange(PublishChangeBo data) {
        Screen screen = getAndCheckBigScreen(data.getId());
        if (Screen.STATE_RUNNING.equals(data.getState())) {//发布状态
            List<ScreenApi> screenApis=screenApiData.findByScreenId(screen.getId());
            if(screenApis==null||screenApis.size()==0){
                throw new BizException(ErrCode.API_LIST_BLANK);
            }
            screen.setState(Screen.STATE_RUNNING);
            screenManager.register(screen);
            screenManager.publish(screen);
        } else {//取消发布
            screen.setState(Screen.STATE_STOPPED);
            screenManager.unPublish(screen);
        }
        screenData.save(screen);
    }

    @Override
    public void setDefaultScreen(Long id) {
        Screen screen = getAndCheckBigScreen(id);
        Screen oldScreen=screenData.findByIsDefault(true);
        if(oldScreen!=null){
            oldScreen.setIsDefault(false);
        }
        screenData.save(oldScreen);
        screen.setIsDefault(true);
        screenData.save(screen);
    }

    @Override
    public void deleteScreen(Long id) {
        Screen screen = getAndCheckBigScreen(id);
        try {
            Path path = Paths.get(String.format("%s/%s", screenConfig.getScreenDir(), id))
                    .toAbsolutePath().normalize();
            File file = path.toFile();
            try {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.delete(file);
                }
            } catch (NoSuchFileException e) {
                log.warn("delete big screen resource error", e);
            }
            screenData.deleteById(screen.getId());
        } catch (Throwable e) {
            throw new BizException(ErrCode.DELETE_BIG_SCREEN_ERROR, e);
        }
    }

    @Override
    public Paging<Screen> getBigScreens(PageRequest<Screen> request) {
        return screenData.findAll(request);
    }

    private Screen getAndCheckBigScreen(Long id) {
        Screen oldBigScreen = screenData.findById(id);
        if (oldBigScreen == null) {
            throw new BizException(ErrCode.BIG_SCREEN_NOT_FOUND);
        }
        dataOwnerService.checkOwner(oldBigScreen);
        return oldBigScreen;
    }
}
