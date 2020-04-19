package com.sp.service.lock;

import com.google.common.collect.Lists;
import com.sp.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Created by admin on 2020/3/31.
 */

@Slf4j
public class RedisLuaDisLock {

    private static final Long SUCCESS = 1L;

    private static RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringBeanUtil.getBean("redisTemplate");


    /**
     * 获取锁-lua
     *
     * @param key        锁的key
     * @param value      防止错误释放锁 (随机生成)
     * @param expireTime 锁的超时时间(毫秒)
     * @return
     */

    public static boolean getLock(String key, String value, int expireTime) {
        String script = "if redis.call('setNX',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Lists.newArrayList(key), value, expireTime);

        return SUCCESS.equals(result);
    }


    /**
     * 获取锁-lua
     *
     * @param key        锁的key
     * @param value      防止错误释放锁 (随机生成)
     * @param expireTime 锁的超时时间(毫秒)
     * @param retryTimes 获取锁的重试次数，默认1次
     * @return
     */
    public static boolean getLock(String key, String value, int expireTime, int retryTimes) {
        String script = "if redis.call('setNX',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

        if (retryTimes <= 0) {
            retryTimes = 1;
        }
        try {
            for (int count = 0; count < retryTimes; count++) {
                Long result = redisTemplate.execute(redisScript, Lists.newArrayList(key), value, expireTime);

                if (SUCCESS.equals(result)) {
                    return true;
                } else {
                    if (retryTimes == count) {
                        log.warn("重试 {} 次后，获取锁失败， key:{}, requestId:{}", count, key, value);
                        return false;
                    } else {
                        log.warn("重试 {} 次后，获取锁成功 key:{},requestId:{}", count, key, value);
                        Thread.sleep(100);
                        continue;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("获取锁异常， key:{}, requestId:{}", key, value);
        }

        return false;
    }

    /**
     * 解锁
     *
     * @param key   锁的key
     * @param value 防止错误释放锁 (随机生成)
     * @return
     */
    public static boolean releaseLock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Lists.newArrayList(key), value);

        return SUCCESS.equals(result);
    }
}
