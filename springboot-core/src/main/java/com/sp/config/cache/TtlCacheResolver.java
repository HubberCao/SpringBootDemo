package com.sp.config.cache;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;


public class TtlCacheResolver extends SimpleCacheResolver {

    private static Map<String, RedisCache> cacheMap = new LinkedHashMap<>();
    private final static int MAX_CACHE_SIZE = 1024;

    public TtlCacheResolver() {
    }

    public TtlCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = this.getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        } else {
            Collection<Cache> result = new ArrayList(cacheNames.size());
            Iterator iterator = cacheNames.iterator();

            while (iterator.hasNext()) {
                String cacheName = (String) iterator.next();
                Cache cache = getCacheFromMem(context, cacheName);
                if (cache == null) {
                    cache = this.getCacheManager().getCache(cacheName);
                }
                if (cache == null) {
                    throw new IllegalArgumentException(
                            "Cannot find cache named '" + cacheName + "' for " + context.getOperation());
                }
                result.add(cache);
            }

            return result;
        }
    }

    /**
     * 通过反射获取ttl, 并动态生成一个RedisCacheConfiguration注入到RedisCache实例当中，这样RedisCache实例就是我们声明式注解配置的ttl时间了
     * @param context
     * @param cacheName
     * @return
     */
    private Cache getCacheFromMem(CacheOperationInvocationContext<?> context, String cacheName) {
        Long ttl = this.getTtl(context);
        if (ttl != null && ttl > 0) {
            String cacheKey = cacheName + "-" + ttl;
            RedisCache cache = cacheMap.get(cacheKey);
            this.clearIfOverload();
            if (cache == null) {
                cache = this.createRedisCache(cacheName, ttl);
                if (cache != null) {
                    cacheMap.putIfAbsent(cacheKey, cache);
                }
            }
            return cache;
        }
        return null;
    }

    private Long getTtl(CacheOperationInvocationContext<?> context) {
        Method method = context.getMethod();
        //Long ttl = null;
        if (method != null) {
            CacheableTtl cacheableTtl = method.getAnnotation(CacheableTtl.class);
            if (cacheableTtl != null) {
                return cacheableTtl.ttl();
            }
            CachePutTtl cachePutTtl = method.getAnnotation(CachePutTtl.class);
            if (cachePutTtl != null) {
                return cachePutTtl.ttl();
            }
        }
        return null;
    }

    private RedisCache createRedisCache(String cacheName, Long ttl) {
        CacheManager cacheManager = super.getCacheManager();
        if (cacheManager instanceof TtlRedisCacheManager) {
            TtlRedisCacheManager manager = (TtlRedisCacheManager) cacheManager;
            RedisCacheConfiguration configuration = manager.getCacheConfigurationCopy();
            return manager.createCache(cacheName, configuration.entryTtl(Duration.ofSeconds(ttl)));
        }
        return null;
    }

    private void clearIfOverload() {
        if (cacheMap.size() > MAX_CACHE_SIZE) {
            cacheMap.clear();
        }
    }
}