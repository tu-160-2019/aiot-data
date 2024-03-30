/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.plugins.http.service;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.EventLevel;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.EventReport;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugins.http.conf.HttpConfig;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * mqtt官方协议文档：
 * http://iotkit-open-source.gitee.io/document/pages/device_protocol/http/#%E4%BA%8B%E4%BB%B6%E4%B8%8A%E6%8A%A5
 *
 * @author sjg
 */
@Slf4j
@Component
@Data
public class HttpVerticle extends AbstractVerticle implements Handler<RoutingContext> {
    private HttpConfig config;
    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;
    @Autowired
    private PluginInfo pluginInfo;
    private static final Set<String> DEVICE_ONLINE = new HashSet<>();
    private HttpServer httpServer;

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().schedule(this::initHttpServer, 3, TimeUnit.SECONDS);
    }

    private void initHttpServer() {
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()).handler(this);
        httpServer.requestHandler(router).listen(config.getPort(), ar -> {
            if (ar.succeeded()) {
                log.info("http server is listening on port " + ar.result().actualPort());
            } else {
                log.error("Error on starting the server", ar.cause());
            }
        });
    }

    @Override
    public void stop() {
        httpServer.close(rst -> {
            log.info("http server close:{}", rst.succeeded());
        });
    }

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "application/json");
        response.setStatusCode(200);

        try {
            String secret = ctx.request().getHeader("secret");
            if (StringUtils.isBlank(secret)) {
                log.error("secret不能为空");
                response.setStatusCode(401);
                end(response);
                return;
            }

            HttpServerRequest request = ctx.request();
            // /sys/{productKey}/{deviceName}/properties
            String path = request.path();
            String[] parts = path.split("/");
            if (parts.length < 5) {
                log.error("不正确的路径");
                response.setStatusCode(500);
            }

            String productKey = parts[2];
            String deviceName = parts[3];
            String type = parts[4];
            ThingDevice device = thingService.getDevice(deviceName);
            if (device == null) {
                log.error("认证失败，设备:{} 不存在", deviceName);
                response.setStatusCode(401);
                end(response);
                return;
            }
            if (!secret.equalsIgnoreCase(device.getSecret())) {
                log.error("认证失败，secret不正确，期望值:{}", device.getSecret());
                response.setStatusCode(401);
                end(response);
                return;
            }

            //设备上线
            if (!DEVICE_ONLINE.contains(deviceName)) {
                thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                        .id(UUID.randomUUID().toString())
                        .productKey(productKey)
                        .deviceName(deviceName)
                        .state(DeviceState.ONLINE)
                        .time(System.currentTimeMillis())
                        .build());
                DEVICE_ONLINE.add(deviceName);
            }

            String method = request.method().name();
            JsonObject payload = ctx.getBodyAsJson();

            if ("event".equals(type)) {
                //事件上报
                if ("POST".equalsIgnoreCase(method)) {
                    response.setStatusCode(500);
                    log.error("请求类型不正确，期望值:POST，实际值:{}", method);
                    end(response);
                }
                thingService.post(
                        pluginInfo.getPluginId(),
                        EventReport.builder()
                                .id(payload.getString("id"))
                                .productKey(productKey)
                                .deviceName(deviceName)
                                .level(EventLevel.INFO)
                                .name(parts[3])
                                .params(payload.getJsonObject("params").getMap())
                                .time(System.currentTimeMillis())
                                .build()
                );
                end(response);
                return;
            }

            if ("properties".equals(type)) {
                if ("POST".equalsIgnoreCase(method)) {
                    //属性上报
                    thingService.post(
                            pluginInfo.getPluginId(),
                            PropertyReport.builder()
                                    .id(UUID.randomUUID().toString())
                                    .productKey(productKey)
                                    .deviceName(deviceName)
                                    .params(payload.getJsonObject("params").getMap())
                                    .time(System.currentTimeMillis())
                                    .build()
                    );
                    end(response);
                    return;
                }

                if ("GET".equalsIgnoreCase(method)) {
                    //属性获取
                    Map<String, ?> property = thingService.getProperty(deviceName);
                    response.end(new JsonObject()
                            .put("code", 0)
                            .put("data", property)
                            .toString());
                }
            }
        } catch (Exception e) {
            log.error("消息处理失败", e);
            response.setStatusCode(500);
            end(response);
        }
    }

    private void end(HttpServerResponse response) {
        response.end(new JsonObject()
                .put("code", response.getStatusCode() == 200 ? 0 : response.getStatusCode())
                .toString());
    }

}
