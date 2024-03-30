package cc.iotkit.plugins.hydrovalve.service;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusConstants;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusEntity;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusRtuAnalysis;
import cc.iotkit.plugins.hydrovalve.conf.HydrovalveConfig;
import cc.iotkit.plugins.hydrovalve.utils.ByteUtils;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author：tfd
 * @Date：2024/1/9 17:06
 */
@Slf4j
@Service
public class ModbusVerticle extends AbstractVerticle {
    @Getter
    @Setter
    private HydrovalveConfig modbusConfig;

    private NetClient netClient;

    private NetSocket socket;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    ModBusRtuAnalysis analysis=new ModBusRtuAnalysis();

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
        NetClientOptions options = new NetClientOptions();
        options.setReconnectAttempts(Integer.MAX_VALUE);
        options.setReconnectInterval(20000L);
        netClient = vertx.createNetClient(options);
        netClient.connect(modbusConfig.getPort(), modbusConfig.getHost())
                .onComplete(result -> {
                    if (result.succeeded()) {
                        connectState = 2;
                        log.info("connect modbus slave success");
                        socket = result.result();
                        thingService.post(pluginInfo.getPluginId(), DeviceRegister.builder()
                                .id(UUID.randomUUID().toString())
                                .productKey("PYWH4r8xBzsfn3XB")
                                .deviceName(String.format("modbus_%d", 1))
                                .build());
                        stateChange(DeviceState.ONLINE,String.format("modbus_%d", 1));
                        socket.handler(data -> {
                            String hexStr = ByteUtils.BinaryToHexString(data.getBytes(), false);
                            log.info("modbus received message:{}", hexStr);
                            //获取功能码
                            if(0x03==data.getBytes()[1]){
                                ModBusEntity ret = analysis.unPackCmd2Entity(data.getBytes());
                                Map<String, Object> params = new HashMap<>();
                                params.put("devSwith" , Integer.parseInt(ByteUtils.BinaryToHexString(getData(ret.getData()),false)));//数据标识
                                thingService.post(pluginInfo.getPluginId(),
                                        PropertyReport.builder().deviceName(String.format("modbus_%d", ret.getDevAddr())).productKey("PwMfpXmp4ZWkGahn")
                                                .params(params)
                                                .build()
                                );
                            }
                        }).closeHandler(res -> {
                                    connectState = 0;
                                    vertx.cancelTimer(timerID);
                                    log.info("modbus tcp connection closed!");
                                    stateChange(DeviceState.OFFLINE,String.format("modbus_%d", 1));
                                }
                        ).exceptionHandler(res -> {
                            connectState = 0;
                            vertx.cancelTimer(timerID);
                            log.info("modbus tcp connection exce!");
                            stateChange(DeviceState.OFFLINE,String.format("modbus_%d", 1));
                        });
                        timerID = vertx.setPeriodic(modbusConfig.getInterval(), t -> {
                            readDataTask();
                        });
                    } else {
                        connectState = 0;
                        log.info("connect modbus tcp error", result.cause());
                    }
                })
                .onFailure(e -> {
                    log.error("modbus connect failed", e);
                    connectState = 0;
                })
        ;
    }

    private void readDataTask() {
        log.info("readData:" + socket);
        if (socket != null) {
            ModBusEntity read=new ModBusEntity();
            read.setFunc(ModBusConstants.FUN_CODE3);
            read.setDevAddr((byte) 1);
            Integer addr=1;
            Integer length=1;
            String a1= StringUtils.leftPad(addr.toHexString(addr),4,'0')+StringUtils.leftPad(addr.toHexString(length),4,'0');
            read.setData(ByteUtils.hexStrToBinaryStr(a1));
            byte[] msg = analysis.packCmd4Entity(read);
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

    private byte[] getData(byte[] data) {
        int lenth=data[0];
        byte[] val = new byte[lenth];
        System.arraycopy(data, 1, val, 0, lenth);
        return val;
    }

    private void stateChange(DeviceState state,String deviceName) {
        thingService.post(pluginInfo.getPluginId(),
                DeviceStateChange.builder()
                        .id(IdUtil.simpleUUID())
                        .state(state).productKey("PYWH4r8xBzsfn3XB").deviceName(deviceName)
                        .time(System.currentTimeMillis())
                        .build());
    }

    public void sendMsg(byte[] msg) {
        log.info("modbus send msg data:{}", ByteUtils.BinaryToHexString(msg,false));
        Buffer data = Buffer.buffer(msg);
        socket.write(data, r -> {
            if (r.succeeded()) {
                log.info("modbus msg send success:{}", ByteUtils.BinaryToHexString(msg,false));
            } else {
                log.error("modbus msg send failed", r.cause());
            }
        });
    }
}
