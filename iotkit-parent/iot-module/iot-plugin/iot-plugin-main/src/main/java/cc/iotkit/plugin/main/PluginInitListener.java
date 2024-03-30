package cc.iotkit.plugin.main;

import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.data.manager.IPluginInstanceData;
import cc.iotkit.model.plugin.PluginInstance;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author starBlues
 * @version 1.0
 */
@Component
@Slf4j
public class PluginInitListener implements PluginInitializerListener {
    private boolean isCompleted = false;

    /**
     * 插件主程序运行ip
     */
    @Value("${plugin.ip:127.0.0.1}")
    private String mainIp;

    /**
     * 插件主程序web端口
     */
    @Value("${server.port:8087}")
    private int port;

    @Autowired
    private PluginOperator pluginOperator;

    @Autowired
    private IPluginInfoData pluginInfoData;

    @Autowired
    private IPluginInstanceData pluginInstanceData;

    @Override
    public void before() {
        log.info("before plugin init");
    }

    @Override
    public void complete() {
        log.info("plugin init finished");
        this.isCompleted = true;
    }

    @Override
    public void failure(Throwable e) {
        log.info("plugin init failed", e);
    }

    /**
     * 插件注册，保活
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    private void keepAlive() {
        if (!isCompleted) {
            return;
        }
        //获取插件列表
        List<PluginInfo> plugins = pluginOperator.getPluginInfo();
        for (PluginInfo plugin : plugins) {
            cc.iotkit.model.plugin.PluginInfo pluginInfo = pluginInfoData.findByPluginId(plugin.getPluginId());
            if (pluginInfo == null) {
                continue;
            }

            PluginInstance instance = pluginInstanceData.findInstance(IPluginMain.MAIN_ID, plugin.getPluginId());
            if (instance == null) {
                instance = PluginInstance.builder()
                        .mainId(IPluginMain.MAIN_ID)
                        .pluginId(pluginInfo.getId())
                        .ip(mainIp)
                        .port(port)
                        .build();
            }
            //更新心跳时间
            instance.setHeartbeatAt(System.currentTimeMillis());
            pluginInstanceData.save(instance);
        }

    }
}
