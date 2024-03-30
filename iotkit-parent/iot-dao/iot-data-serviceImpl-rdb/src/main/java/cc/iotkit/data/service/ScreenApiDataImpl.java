package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.ScreenApiRepository;
import cc.iotkit.data.manager.IScreenApiData;
import cc.iotkit.data.model.TbScreenApi;
import cc.iotkit.model.screen.ScreenApi;
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
public class ScreenApiDataImpl implements IScreenApiData,IJPACommData<ScreenApi,Long> {

    @Autowired
    private ScreenApiRepository screenApiRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return screenApiRepository;
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
        return MapstructUtils.convert(screenApiRepository.findByScreenId(id),ScreenApi.class);
    }

    @Override
    public void deleteByScreenId(Long id) {
        screenApiRepository.deleteByScreenId(id);
    }
}
