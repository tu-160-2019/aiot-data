/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.test.mqtt.performance;

import cc.iotkit.test.mqtt.config.MqttConfig;
import cc.iotkit.test.mqtt.service.Gateway;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 连接压力测试
 */
@Slf4j
public class ConnectionTest {

    @Autowired
    private MqttConfig mqttConfig;

    @Value("${start:0}")
    private int start;

    @Value("${end:10}")
    private int end;

    @SneakyThrows
    @PostConstruct
    public void init() {

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = start; i < end; i++) {
            int finalI = i;
            executor.submit(() -> {
                log.info("start gateway " + (finalI + 1));
                Gateway gateway = new Gateway(mqttConfig, "hbtgIA0SuVw9lxjB", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU",
                        "TEST:GW:T" + StringUtils.leftPad(finalI + "", 6, "0"));

//                gateway.addSubDevice("Rf4QSjbm65X45753",
//                        "TEST_SW_" + StringUtils.leftPad(finalI + "", 6, "0"),
//                        "S01");
//
//                gateway.addSubDevice("cGCrkK7Ex4FESAwe",
//                        "TEST_SC_" + StringUtils.leftPad(finalI + "", 6, "0"),
//                        "S01");
//
//                gateway.addSubDevice("xpsYHExTKPFaQMS7",
//                        "TEST_LT_" + StringUtils.leftPad(finalI + "", 6, "0"),
//                        "L01");

                gateway.start();
            });
        }

        System.in.read();
    }
}
