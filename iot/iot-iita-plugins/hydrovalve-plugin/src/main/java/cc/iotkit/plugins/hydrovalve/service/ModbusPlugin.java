package cc.iotkit.plugins.hydrovalve.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugins.hydrovalve.conf.HydrovalveConfig;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.core.PluginCloseType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author：tfd
 * @Date：2024/1/8 14:57
 */
@Slf4j
@Service
public class ModbusPlugin implements PluginCloseListener {

    @Autowired
    private PluginInfo pluginInfo;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginScript pluginScript;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginConfig pluginConfig;

    @Autowired
    private HydrovalveConfig modbusConfig;

    @Autowired
    private ModbusVerticle modbusVerticle;

    private Vertx vertx;
    private String deployedId;

    @PostConstruct
    public void init(){
        vertx = Vertx.vertx();
        try {
            //获取插件最新配置替换当前配置
            Map<String, Object> config = pluginConfig.getConfig(pluginInfo.getPluginId());
            BeanUtil.copyProperties(config, modbusConfig, CopyOptions.create().ignoreNullValue());
            modbusVerticle.setModbusConfig(modbusConfig);

            Future<String> future = vertx.deployVerticle(modbusVerticle);
            future.onSuccess((s -> {
                deployedId = s;
                log.info("modbus plugin started success,config:"+ JsonUtils.toJsonString(modbusConfig));
            }));
            future.onFailure(Throwable::printStackTrace);
        } catch (Throwable e) {
            log.error("modbus plugin error", e);
        }
    }

    @SneakyThrows
    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        log.info("plugin close,type:{},pluginId:{}", closeType, pluginInfo.getPluginId());
        if (deployedId != null) {
            CountDownLatch wait = new CountDownLatch(1);
            Future<Void> future = vertx.undeploy(deployedId);
            future.onSuccess(unused -> {
                log.info("modbus plugin stopped success");
                wait.countDown();
            });
            future.onFailure(h -> {
                log.info("modbus plugin stopped failed");
                h.printStackTrace();
                wait.countDown();
            });
            wait.await(5, TimeUnit.SECONDS);
        }
    }

}
