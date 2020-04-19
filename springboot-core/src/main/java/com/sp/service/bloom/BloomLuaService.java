package com.sp.service.bloom;

import com.google.common.collect.Lists;
import com.sp.util.SpringBeanUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Created by admin on 2020/3/31.
 */

public class BloomLuaService {

    private static RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringBeanUtil.getBean("redisTemplate");

    /**
     * 布隆过滤器
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean addBloomFilter(String key, String value) {
        String script = "return redis.call('bf.add',KEYS[1],ARGV[1])";
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
        return redisTemplate.execute(redisScript, Lists.newArrayList(key), value);
    }

    public static boolean includeByBloomFilter(String key, String value) {
        String scriptEx = "return redis.call('bf.exists',KEYS[1],ARGV[1])";
        DefaultRedisScript<Boolean> redisScript1 = new DefaultRedisScript<>(scriptEx, Boolean.class);
        return redisTemplate.execute(redisScript1, Lists.newArrayList(key), value);
    }
}
