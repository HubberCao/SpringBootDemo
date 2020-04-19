package com.sp.service.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2020/3/30.
 */

@Component
public class RedissonDisLock {

    @Autowired
    private RedissonClient redissonClient;

    private RedissonDisLock() {
    }

    public static RedissonDisLock getInstance() {
        return RedissonDisLockHolder.redissonDisLock;
    }

    private static class RedissonDisLockHolder {
        private static RedissonDisLock redissonDisLock = new RedissonDisLock();
    }


    public void lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
    }


    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }


    public void lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
    }


    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param unit      单位时间 默认毫秒
     * @param leaseTime 超时时间 默认10毫秒
     * @return
     * @throws InterruptedException
     */

    public boolean tryLock(String lockKey, long leaseTime, TimeUnit unit) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        if (unit == null) {
            unit = TimeUnit.MILLISECONDS;
            leaseTime = 10;
        }
        return lock.tryLock(leaseTime, unit);
    }
}

