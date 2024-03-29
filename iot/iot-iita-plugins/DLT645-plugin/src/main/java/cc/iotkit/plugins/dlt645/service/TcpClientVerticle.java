package cc.iotkit.plugins.dlt645.service;

import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugins.dlt645.analysis.DLT645Analysis;
import cc.iotkit.plugins.dlt645.analysis.DLT645Converter;
import cc.iotkit.plugins.dlt645.analysis.DLT645FunCode;
import cc.iotkit.plugins.dlt645.analysis.DLT645V2007Data;
import cc.iotkit.plugins.dlt645.conf.TcpClientConfig;
import cc.iotkit.plugins.dlt645.constants.DLT645Constant;
import cc.iotkit.plugins.dlt645.utils.ByteUtils;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:00
 */
@Slf4j
@Service
public class TcpClientVerticle extends AbstractVerticle {
    @Getter
    @Setter
    private TcpClientConfig config;

    private NetClient netClient;

    private NetSocket socket;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    private int connectState = 0;

    private long timerID;

    @Override
    public void start() {
        log.info("init start");
    }

    @Scheduled(initialDelay = 2, fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void initClient() {
        if (connectState > 0) {
            return;
        }
        connectState = 1;

        DLT645Analysis.inst().getTemplateByDIn(DLT645Constant.PRO_VER_2007);
        NetClientOptions options = new NetClientOptions();
        options.setReconnectAttempts(Integer.MAX_VALUE);
        options.setReconnectInterval(20000L);
        netClient = vertx.createNetClient(options);
        netClient.connect(config.getPort(), config.getHost())
                .onComplete(result -> {
                    if (result.succeeded()) {
                        connectState = 2;
                        log.info("connect dlt645 server success");
                        socket = result.result();
                        stateChange(DeviceState.ONLINE);
                        socket.handler(data -> {
                            String hexStr = ByteUtils.byteArrayToHexString(data.getBytes(), false);
                            log.info("Received message:{}", hexStr);
                            Map<String, Object> ret = DLT645Analysis.unPackCmd2Map(ByteUtils.hexStringToByteArray(hexStr));
                            //获取功能码
                            Object func = ret.get(DLT645Analysis.FUN);
                            DLT645FunCode funCode = DLT645FunCode.decodeEntity((byte) func);
                            if (funCode.isError()) {
                                log.info("message erroe:{}", hexStr);
                                return;
                            }
                            //获取设备地址
                            byte[] adrrTmp = (byte[]) ret.get(DLT645Analysis.ADR);
                            byte[] addr = new byte[6];
                            ByteUtils.byteInvertedOrder(adrrTmp, addr);
                            //获取数据
                            byte[] dat = (byte[]) ret.get(DLT645Analysis.DAT);
                            String strAddr=ByteUtils.byteArrayToHexString(addr,false);
                            DLT645V2007Data dataEntity = new DLT645V2007Data();
                            dataEntity.decodeValue(dat, DLT645Analysis.din2entity);
                            Map<String, Object> params = new HashMap<>();
                            params.put("p" + dataEntity.getKey(), dataEntity.getValue());//数据标识
                            thingService.post(pluginInfo.getPluginId(),
                                    PropertyReport.builder().deviceName(strAddr).productKey("PwMfpXmp4ZWkGahn")
                                            .params(params)
                                            .build()
                            );
                        }).closeHandler(res -> {
                                    connectState = 0;
                                    vertx.cancelTimer(timerID);
                                    log.info("dlt645 tcp connection closed!");
                                    stateChange(DeviceState.OFFLINE);
                                }
                        ).exceptionHandler(res -> {
                            connectState = 0;
                            vertx.cancelTimer(timerID);
                            log.info("dlt645 tcp connection exce!");
                            stateChange(DeviceState.OFFLINE);
                        });
                        timerID = vertx.setPeriodic(config.getInterval(), t -> {
                            readDataTask();
                        });
                    } else {
                        connectState = 0;
                        log.info("connect dlt645 tcp error", result.cause());
                    }
                })
                .onFailure(e -> {
                    log.error("connect failed", e);
                    connectState = 0;
                })
        ;
    }

    private void readDataTask() {
        log.info("readData:" + socket);
        if (socket != null) {
            String msg = DLT645Converter.packData("000023092701", "读数据", "00000000");
            sendMsg(msg);
        }
    }

    @Override
    public void stop() throws Exception {
        if (netClient != null) {
            netClient.close();
        }
        vertx.cancelTimer(timerID);
        connectState = 0;
        super.stop();
    }

    private void stateChange(DeviceState state) {
        thingService.post(pluginInfo.getPluginId(),
                applyPkDn(DeviceStateChange.builder()
                        .id(IdUtil.simpleUUID())
                        .state(state)
                        .time(System.currentTimeMillis())
                        .build()));
    }

    private IDeviceAction applyPkDn(IDeviceAction action) {
        action.setProductKey("BRD3x4fkKxkaxXFt");
        action.setDeviceName("WG123456");
        return action;
    }

    public void sendMsg(String msg) {
        log.info("send msg data:{}", msg);
        Buffer data = Buffer.buffer(HexUtil.decodeHex(msg));
        socket.write(data, r -> {
            if (r.succeeded()) {
                log.info("msg send success:{}", msg);
            } else {
                log.error("msg send failed", r.cause());
            }
        });
    }

}
