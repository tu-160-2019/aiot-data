package cc.iotkit.plugins.emqx.service;
/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */

import cc.iotkit.common.utils.CodecUtil;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AuthVerticle extends AbstractVerticle {

    private HttpServer backendServer;

    @Setter
    private int port;

    @Setter
    private String serverPassword;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    @Override
    public void start() {
        backendServer = vertx.createHttpServer();

        //第一步 声明Router&初始化Router
        Router backendRouter = Router.router(vertx);
        //获取body参数，得先添加这句
        backendRouter.route().handler(BodyHandler.create());

        //第二步 配置Router解析url
        backendRouter.route(HttpMethod.POST, "/mqtt/auth").handler(rc -> {
            JsonObject json = rc.getBodyAsJson();
            log.info("mqtt auth:{}", json);
            try {
                String clientId = json.getString("clientid");
                String username = json.getString("username");
                String password = json.getString("password");

                //服务端插件连接
                if (clientId.equals("server") && serverPassword.equals(password)) {
                    httpResult(rc.response(), 200);
                    return;
                }

                //其它客户端连接
                String[] parts = clientId.split("_");
                if (parts.length < 3) {
                    log.error("clientid:{}不正确", clientId);
                    httpResult(rc.response(), 400);
                    return;
                }

                log.info("MQTT client auth,clientId:{},username:{},password:{}",
                        clientId, username, password);

                String productKey = parts[0];
                String deviceName = parts[1];
                String gwModel = parts[2];
                if (!username.equals(deviceName)) {
                    log.error("username:{}不正确", deviceName);
                    httpResult(rc.response(), 403);
                    return;
                }

                ThingProduct product = thingService.getProduct(productKey);
                if (product == null) {
                    log.error("获取产品信息失败,productKey:{}", productKey);
                    httpResult(rc.response(), 403);
                    return;
                }

                String validPasswd = CodecUtil.md5Str(product.getProductSecret() + clientId);
                if (!validPasswd.equalsIgnoreCase(password)) {
                    log.error("密码验证失败,期望值:{}", validPasswd);
                    httpResult(rc.response(), 403);
                    return;
                }

                //网关设备注册
                ActionResult result = thingService.post(
                        pluginInfo.getPluginId(),
                        DeviceRegister.builder()
                                .productKey(productKey)
                                .deviceName(deviceName)
                                .model(gwModel)
                                .version("1.0")
                                .id(UniqueIdUtil.newRequestId())
                                .time(System.currentTimeMillis())
                                .build()
                );
                if (result.getCode() != 0) {
                    log.error("设备注册失败:{}", result);
                    httpResult(rc.response(), 403);
                    return;
                }

                Set<String> devices = new HashSet<>();
                devices.add(productKey + "," + deviceName);
                EmqxPlugin.CLIENT_DEVICE_MAP.putIfAbsent(productKey + deviceName, devices);

                httpResult(rc.response(), 200);
            } catch (Throwable e) {
                httpResult(rc.response(), 500);
                log.error("mqtt auth failed", e);
            }
        });
        backendRouter.route(HttpMethod.POST, "/mqtt/acl").handler(rc -> {
            String json = rc.getBodyAsString();
            log.info("mqtt acl:{}", json);
            try {
                httpResult(rc.response(), 200);
            } catch (Throwable e) {
                httpResult(rc.response(), 500);
                log.error("mqtt acl failed", e);
            }
        });

        backendServer.requestHandler(backendRouter)
                .listen(port, "0.0.0.0")
                .onSuccess(s -> {
                    log.info("auth server start success,port:{}", s.actualPort());
                }).onFailure(e -> {
                    e.printStackTrace();
                })
        ;
    }

    private void httpResult(HttpServerResponse response, int code) {
        response.putHeader("Content-Type", "application/json");
        response
                .setStatusCode(code);
        response
                .end("{\"result\": \"" + (code == 200 ? "allow" : "deny") + "\"}");
    }

    @Override
    public void stop() throws Exception {
        backendServer.close(voidAsyncResult -> log.info("close emqx auth server..."));
    }
}
