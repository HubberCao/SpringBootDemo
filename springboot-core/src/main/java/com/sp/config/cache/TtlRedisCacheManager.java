package com.sp.config.cache;

import java.util.Map;
import java.util.Objects;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * Created by admin on 2020/4/8.
 */
public class TtlRedisCacheManager extends RedisCacheManager {

    private final RedisCacheConfiguration defaultCacheConfiguration;

    // 不注释，会在类上报Class doesn't contain matching constructor for autowiring
    /*
    public TtlRedisCacheManager(RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        Objects.requireNonNull(defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }

    public TtlRedisCacheManager(RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
            String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
        Objects.requireNonNull(defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }

    public TtlRedisCacheManager(RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
    boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
        Objects.requireNonNull(defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }*/

    public TtlRedisCacheManager(RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
            Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
        Objects.requireNonNull(defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }

    /*public TtlRedisCacheManager(RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
            Map<String, RedisCacheConfiguration> initialCacheConfigurations,
    boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        Objects.requireNonNull(defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }*/

    /**
     * 这个方法尤为重要，加入自定义RedisCacheManager的原因就是RedisCacheManager
     * 的createCache为protected的，导致自定义的CacheResolver无法调用，因此重写此方法，
     * 使其在CacheResolver可以被调用
     *
     * @param cacheName
     * @param config
     * @return
     */
    public RedisCache createCache(String cacheName, RedisCacheConfiguration config) {
        return super.createRedisCache(cacheName, config);
    }

    public RedisCacheConfiguration getCacheConfigurationCopy() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultCacheConfiguration.getTtl())
                .serializeKeysWith(defaultCacheConfiguration.getKeySerializationPair())
                .serializeValuesWith(defaultCacheConfiguration.getValueSerializationPair())
                .withConversionService(defaultCacheConfiguration.getConversionService())
                .computePrefixWith(cacheName -> defaultCacheConfiguration.getKeyPrefixFor(cacheName));
    }
}
