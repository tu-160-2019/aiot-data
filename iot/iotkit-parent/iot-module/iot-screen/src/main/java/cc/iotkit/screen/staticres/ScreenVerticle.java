package cc.iotkit.screen.staticres;

import cc.iotkit.screen.api.ScreenApiHandle;
import cc.iotkit.screen.config.ScreenConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：tfd
 * @Date：2023/6/25 16:03
 */
@Slf4j
public class ScreenVerticle extends AbstractVerticle {

    private HttpServer httpServer;

    private int port;

    private String packageName;

    private ScreenApiHandle apiHandler;

    private ScreenConfig screenConfig;

    public ScreenVerticle(int port,String packageName,ScreenConfig screenConfig) {
        this.port = port;
        this.packageName = packageName;
        this.screenConfig = screenConfig;
    }

    public void setApiHandler(ScreenApiHandle apiHandler) {
        this.apiHandler = apiHandler;
    }

    @Override
    public void start() {
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route(screenConfig.screenAdmin + "/*").handler(StaticHandler.create(screenConfig.getScreenDir() + "/" + apiHandler.getScreenId() + "/" + packageName));
        router.get(screenConfig.screenAdmin).handler(ctx -> {
            ctx.response().sendFile(screenConfig.getScreenDir() + "/" + apiHandler.getScreenId() + "/" + packageName + "/index.html");
        });
        router.get("/*").handler(ctx -> {
            String res = apiHandler.httpReq(ctx.request(), ctx.response());
            ctx.response().end(res);
        });
        router.post("/*").handler(BodyHandler.create()).handler(ctx -> {
            String res = apiHandler.httpReq(ctx.request(), ctx.response());
            ctx.response().end(res);
        });
        httpServer.requestHandler(router).listen(port, (http) -> {
            if (http.succeeded()) {
                log.info("screen server create succeed,port:{}", port);
            } else {
                log.error("screen server create failed", http.cause());
            }
        });
    }

    @Override
    public void stop() {
        httpServer.close(voidAsyncResult -> log.info("close screen server..."));
    }
}
