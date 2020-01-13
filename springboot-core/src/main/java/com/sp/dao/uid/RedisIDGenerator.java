package com.sp.dao.uid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

/**
 * 基于redis的分布式ID生成器
 * Created by admin on 2019/12/11.
 */
@Component
public class RedisIDGenerator implements IDGenerator {

    @Autowired
    private RedisAtomicLong redisAtomicLong;

    @Override
    public String getNextId() {
        return String.valueOf(redisAtomicLong.incrementAndGet());
    }
}
