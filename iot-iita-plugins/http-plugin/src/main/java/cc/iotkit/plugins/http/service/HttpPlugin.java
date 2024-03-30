package cc.iotkit.plugins.http.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugins.http.conf.HttpConfig;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.core.PluginCloseType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author sjg
 */
@Slf4j
@Service
public class HttpPlugin implements PluginCloseListener {

    @Autowired
    private PluginInfo pluginInfo;
    @Autowired
    private HttpVerticle httpVerticle;
    @Autowired
    private HttpConfig httpConfig;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginConfig pluginConfig;

    private Vertx vertx;
    private String deployedId;

    @PostConstruct
    public void init() {
        vertx = Vertx.vertx();
        try {
            //获取插件最新配置替换当前配置
            Map<String, Object> config = pluginConfig.getConfig(pluginInfo.getPluginId());
            log.info("get config:{}", JsonUtils.toJsonString(config));
            BeanUtil.copyProperties(config, httpConfig, CopyOptions.create().ignoreNullValue());
            httpVerticle.setConfig(httpConfig);

            Future<String> future = vertx.deployVerticle(httpVerticle);
            future.onSuccess((s -> {
                deployedId = s;
                log.info("http plugin startup success");
            }));
            future.onFailure((e) -> {
                log.error("http plugin startup failed", e);
            });
        } catch (Throwable e) {
            log.error("http plugin startup error", e);
        }
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            log.info("plugin close,type:{},pluginId:{}", closeType, pluginInfo.getPluginId());
            if (deployedId != null) {
                CountDownLatch wait = new CountDownLatch(1);
                Future<Void> future = vertx.undeploy(deployedId);
                future.onSuccess(unused -> {
                    log.info("http plugin stopped success");
                    wait.countDown();
                });
                future.onFailure(h -> {
                    log.info("tcp plugin stopped failed");
                    h.printStackTrace();
                    wait.countDown();
                });
                wait.await(5, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            log.error("http plugin stop error", e);
        }
    }

}
