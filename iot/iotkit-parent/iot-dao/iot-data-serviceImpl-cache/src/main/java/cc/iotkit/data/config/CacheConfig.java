///*
// * +----------------------------------------------------------------------
// * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
// * +----------------------------------------------------------------------
// * | Licensed 未经许可不能去掉「奇特物联」相关版权
// * +----------------------------------------------------------------------
// * | Author: xw2sy@163.com
// * +----------------------------------------------------------------------
// */
//package cc.iotkit.data.config;
//
//import cc.iotkit.common.constant.Constants;
//import cc.iotkit.common.redis.manager.PlusSpringCacheManager;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.Set;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//
//    @Bean
//    public RedisCacheConfiguration getRedisCacheConfiguration() {
//        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
//        cacheConfiguration = cacheConfiguration.serializeValuesWith(RedisSerializationContext
//                .SerializationPair.fromSerializer(RedisSerializer.json()));
//        return cacheConfiguration;
//    }
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory factory, RedisCacheConfiguration config) {
//        Set<String> cacheNames = Set.of(Constants.CACHE_DEVICE_INFO, Constants.CACHE_DEVICE_STATS);
//        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
//                Constants.CACHE_DEVICE_INFO, config,
//                Constants.CACHE_PRODUCT, config,
//                Constants.CACHE_OAUTH_CLIENT, config,
//                Constants.CACHE_CATEGORY, config,
//                Constants.CACHE_THING_MODEL, config,
//                Constants.CACHE_SPACE, config,
//                Constants.CACHE_PRODUCT_SCRIPT, config,
//                //统计缓存5分钟
//                Constants.CACHE_DEVICE_STATS, config.entryTtl(Duration.ofMinutes(5))
//        );
//
//        return RedisCacheManager.builder(factory)
//                .initialCacheNames(cacheNames)
//                .withInitialCacheConfigurations(cacheConfigs)
//                .build();
//    }
//
//}
