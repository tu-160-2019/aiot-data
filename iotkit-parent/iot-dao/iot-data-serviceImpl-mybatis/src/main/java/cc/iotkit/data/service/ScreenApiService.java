package cc.iotkit.data.service;

import cc.iotkit.data.model.TbScreenApi;
import cc.iotkit.model.screen.ScreenApi;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ScreenApiService extends IService<TbScreenApi> {
    List<ScreenApi> findByScreenId(Long id);

    void deleteByScreenId(Long id);
}
