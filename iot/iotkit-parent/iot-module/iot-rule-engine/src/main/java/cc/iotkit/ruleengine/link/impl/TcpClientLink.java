package cc.iotkit.ruleengine.link.impl;

import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.link.BaseSinkLink;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author huangwenl
 * @date 2022-12-14
 */
@Slf4j
public class TcpClientLink implements BaseSinkLink {
    public static final String LINK_TYPE = "tcp";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PAYLOAD = "payload";

    private Consumer<Void> closeHandler;
    private NetClient client;
    private NetSocket socket;
    private String host;
    private int port;
    private boolean connecting;
    private boolean normal;

    @Override
    public boolean open(Map<String, Object> config) {
        try {
            AtomicReference<Vertx> vertx = new AtomicReference<>();
            FIUtil.isTotF(Vertx.currentContext() == null).handler(
                    () -> vertx.set(Vertx.vertx()),
                    () -> vertx.set(Vertx.currentContext().owner())
            );
            NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
            client = vertx.get().createNetClient(options);
            port = (int) config.get(PORT);
            host = (String) config.get(HOST);
            connecting = true;
            client.connect(port, host, res -> {
                connecting = false;
                if (res.succeeded()) {
                    log.info("连接成功:{}, {}", port,host);
                    socket = res.result();
                    normal = true;
                    socket.closeHandler(Void -> normal = false);
                } else {
                    closeHandler.accept(null);
                    log.info("连接失败:{}, {}", port,host);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            connecting = false;
            return false;
        }
        return true;
    }

    @Override
    public void send(Map<String, Object> msg, Consumer<String> consumer) {
        FIUtil.isTotF(normal).handler(
                () -> {
                    Future<Void> publish = socket.write(Buffer.buffer(msg.get(PAYLOAD).toString()));
                    try {
                        publish.toCompletionStage().toCompletableFuture().get(300L, TimeUnit.MILLISECONDS);
                        FIUtil.isTotF(publish.succeeded()).handler(
                                () -> consumer.accept(String.format("tcp,发送成功:%s", msg.get(PAYLOAD).toString())),
                                () -> consumer.accept(String.format("tcp,发送失败:%s", msg.get(PAYLOAD).toString()))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        consumer.accept(String.format("tcp,发送异常:%s", msg.get(PAYLOAD).toString()));
                    }
                },
                () -> {
                    consumer.accept("tcp,连接断开,发送失败");
                    if (!connecting) {
                        log.info("tcp重连！");
                        connecting = true;
                        client.connect(port, host, res -> {
                            connecting = false;
                            if (res.succeeded()) {
                                log.info("连接成功:{}, {}", port,host);
                                socket = res.result();
                                normal = true;
                                socket.closeHandler(Void -> normal = false);
                            }
                        });
                    }
                });
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHandler.accept(null);
        }
    }

    @Override
    public void closeHandler(Consumer<Void> consumer) {
        this.closeHandler = consumer;
    }
}
