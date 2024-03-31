package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IScreenApiData;
import cc.iotkit.data.model.TbScreenApi;
import cc.iotkit.model.screen.ScreenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:21
 */
@Primary
@Service
public class ScreenApiDataImpl implements IScreenApiData,IJPACommData<ScreenApi,Long, TbScreenApi> {

    @Autowired
    private ScreenApiService screenApiService;

    @Override
    public ScreenApiService getBaseRepository() {
        return screenApiService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbScreenApi.class;
    }

    @Override
    public Class getTClass() {
        return ScreenApi.class;
    }

    @Override
    public List<ScreenApi> findByScreenId(Long id) {
        return screenApiService.findByScreenId(id);
    }

    @Override
    public void deleteByScreenId(Long id) {
        screenApiService.deleteByScreenId(id);
    }
}
