/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class EmbeddedRedisConfig {

    @Value("${spring.redis.embedded.enabled:false}")
    private  boolean embeddedRedisEnabled;
    @Value("${spring.redis.port:6378}")
    private  Integer redisPort;

    @PostConstruct
    private void start(){
        if (embeddedRedisEnabled) {
            startEmbeddedRedisServer(redisPort);
        }

    }
    public static void startEmbeddedRedisServer(Integer port) {
        RedisServer redisServer;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            redisServer = RedisServer.builder().setting("maxheap 200m")
                    .port(port)
                    .setting("bind localhost")
                    .build();
        } else {
            redisServer = RedisServer.builder()
                    .port(port)
                    .setting("bind localhost")
                    .build();
        }
        try {
            redisServer.start();
        } catch (Exception e) {
            if (e.getMessage().contains("Address already in use")) {
                throw new RuntimeException("redis端口被占用，请先停止本地的redis服务");
            }
            log.error("start redis server failed", e);
        }
    }

}
