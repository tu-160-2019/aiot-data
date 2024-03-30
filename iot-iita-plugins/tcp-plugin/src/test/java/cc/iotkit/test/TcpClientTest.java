package cc.iotkit.test;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpClientTest {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Future<String> future = vertx.deployVerticle(new TcpClientVerticle());
        future.onSuccess((s -> {
            log.info("tcp client started success");
        }));
        future.onFailure((e) -> {
            log.error("tcp client startup failed", e);
        });
    }

}
