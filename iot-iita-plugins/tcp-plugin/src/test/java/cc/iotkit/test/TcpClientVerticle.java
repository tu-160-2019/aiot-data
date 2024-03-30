package cc.iotkit.test;

import cc.iotkit.common.utils.ThreadUtil;
import cc.iotkit.plugins.tcp.parser.DataDecoder;
import cc.iotkit.plugins.tcp.parser.DataEncoder;
import cc.iotkit.plugins.tcp.parser.DataPackage;
import cc.iotkit.plugins.tcp.parser.DataReader;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangwenlong
 * @version 1.0
 * @date 2022/10/23 13:08
 */
@Slf4j
public class TcpClientVerticle extends AbstractVerticle {

    private NetClient netClient;

    private NetSocket socket;

    private String addr = "060101";

    private String pk = "cGCrkK7Ex4FESAwe";

    private AtomicInteger atMid = new AtomicInteger(0);

    @Override
    public void start() {
        initClient();
    }

    @Override
    public void stop() {
        if (null != netClient) {
            netClient.close();
        }
    }

    private void initClient() {
        NetClientOptions options = new NetClientOptions();
        options.setReconnectAttempts(Integer.MAX_VALUE);
        options.setReconnectInterval(20000L);
        netClient = vertx.createNetClient(options);
        RecordParser parser = DataReader.getParser(this::handle);

        netClient.connect(6883, "127.0.0.1", result -> {
            if (result.succeeded()) {
                log.debug("connect tcp success");
                socket = result.result();
                socket.handler(parser);
                //注册
                byte[] pkBytes = pk.getBytes();
                send(DataPackage.builder()
                        .addr(addr)
                        .code(DataPackage.CODE_REGISTER)
                        .mid(getMid())
                        .payload(pkBytes)
                        .build());
            } else {
                log.error("connect tcp error", result.cause());
            }
        });
    }

    private short getMid() {
        atMid.compareAndSet(254, 0);
        return (short) atMid.getAndIncrement();
    }

    private void send(DataPackage data) {
        Buffer buffer = DataEncoder.encode(data);
        log.info("send data:{}", HexUtil.encodeHexStr(buffer.getBytes()));
        socket.write(buffer);
    }

    public void handle(Buffer buffer) {
        log.info("receive server data:{}", buffer.toString());
        DataPackage data = DataDecoder.decode(buffer);
        if (data.getCode() == DataPackage.CODE_REGISTER_REPLY) {
            int rst = Buffer.buffer(data.getPayload()).getInt(0);
            if (rst == 0) {
                log.info("device:{} register success", data.getAddr());
                //定时心跳
                ThreadUtil.newScheduled(1, "heartbeat")
                        .scheduleWithFixedDelay(this::heartbeat, 10, 30, TimeUnit.SECONDS);
                //随机上报数据
                ThreadUtil.newScheduled(1, "reportData")
                        .scheduleWithFixedDelay(this::reportData, 20, 1, TimeUnit.SECONDS);
            }
        }
    }

    private void heartbeat() {
        send(DataPackage.builder()
                .addr(addr)
                .code(DataPackage.CODE_HEARTBEAT)
                .mid(getMid())
                .build());
    }

    private void reportData() {
        if (RandomUtil.randomInt() % 3 == 0) {
            //随机
            return;
        }
        send(DataPackage.builder()
                .addr(addr)
                .code(DataPackage.CODE_DATA_UP)
                .mid(getMid())
                .payload(Buffer.buffer()
                        //rssi
                        .appendShort((short) RandomUtil.randomInt(0, 127))
                        //powerstate
                        .appendByte((byte) (RandomUtil.randomInt() % 2 == 0 ? 1 : 0))
                        .getBytes()
                )
                .build());
    }

}