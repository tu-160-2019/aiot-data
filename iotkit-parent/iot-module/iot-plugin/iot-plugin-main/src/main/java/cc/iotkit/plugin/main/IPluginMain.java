package cc.iotkit.plugin.main;

import cc.iotkit.common.thing.ThingService;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 主程序接口
 *
 * @author sjg
 */
@RestController
@RequestMapping("/plugin")
public interface IPluginMain extends IPluginScript, IPluginConfig {

    /**
     * 主程序id
     */
    String MAIN_ID = UUID.randomUUID().toString();

    /**
     * 调用设备服务
     *
     * @param service 设备服务
     */
    @PostMapping("/invoke")
    void invoke(@RequestBody ThingService<?> service);

}
