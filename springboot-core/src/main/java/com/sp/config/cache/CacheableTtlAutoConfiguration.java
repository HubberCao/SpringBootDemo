package com.sp.config.cache;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2020/4/8.
 */
@Configuration
public class CacheableTtlAutoConfiguration {

    @Bean("redisSerializer")
    @ConditionalOnMissingBean
    public RedisSerializer redisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }

    @Bean("defaultRedisCacheConfiguration")
    @ConditionalOnMissingBean
    public RedisCacheConfiguration defaultRedisCacheConfiguration(RedisSerializer redisSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheWriter redisCacheWriter(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
    }

    @Bean("redisCacheConfigurationMap")
    @ConditionalOnMissingBean
    public Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        return redisCacheConfigurationMap;
    }

    @Bean("ttlRedisCacheManager")
    @ConditionalOnMissingBean
    public TtlRedisCacheManager ttlRedisCacheManager(RedisCacheWriter redisCacheWriter,
                                                     RedisCacheConfiguration defaultRedisCacheConfiguration,
                                                     Map<String, RedisCacheConfiguration> redisCacheConfigurationMap) {
        return new TtlRedisCacheManager(
                redisCacheWriter,
                defaultRedisCacheConfiguration,
                redisCacheConfigurationMap);
    }

    @Bean("ttlCacheResolver")
    @ConditionalOnMissingBean
    public TtlCacheResolver ttlCacheResolver(TtlRedisCacheManager ttlRedisCacheManager) {
        return new TtlCacheResolver(ttlRedisCacheManager);
    }
}