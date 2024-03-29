package cc.iotkit.data.manager;

import cc.iotkit.data.IOwnedData;
import cc.iotkit.model.screen.ScreenApi;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:34
 */
public interface IScreenApiData extends IOwnedData<ScreenApi, Long> {
    List<ScreenApi> findByScreenId(Long id);

    void deleteByScreenId(Long id);
}
