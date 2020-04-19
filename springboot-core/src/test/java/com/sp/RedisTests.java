package com.sp;

import com.sp.common.contants.Constants;
import com.sp.service.lock.RedisLuaDisLock;
import com.sp.service.lock.RedissonDisLock;
import com.sp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by admin on 2020/2/12.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisTests {

    @Autowired
    private RedissonDisLock redissonDisLock;

    ResourceList resourceList = new ResourceList();

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void test() {
        Object value = redisUtils.get(Constants.NO_REPEAT_TOKEN);
        log.info("value:{}", value);
        log.info("" + redisUtils.hasKey(Constants.NO_REPEAT_TOKEN));
    }

    @Test
    public void testHLog() {
        int total = 200;
        /*String key = "user";
        String key2 = "user2";
        RedisUtil.getInstance().del(key);
        RedisUtil.getInstance().del(key2);*/


        /*IntStream.range(0, total).forEach(i -> {
            RedisUtil.getInstance().addHLog(key, "user-" + i);
        });

        IntStream.range(0, 1000).forEach(i -> {
            RedisUtil.getInstance().addHLog(key2, "user-" + i);
        });

        System.out.println(RedisUtil.getInstance().countHLog(key) + "-" + RedisUtil.getInstance().countHLog(key2));

        String destKey = "desHyperLogLog";
        System.out.println(RedisUtil.getInstance().mergeHLog(destKey, key, key2));

        System.out.println(RedisUtil.getInstance().countHLog(destKey));*/

        String key = "HyperLogLog";
        RedisUtil.getInstance().del(key);
        RedisUtil.getInstance().del("hyperLogLog");

    }

    @Test
    public void testLimiter() {
        String userId = "tom123";
        String actionKey = "getApple";
        int period = 1;
        int max = 100;
        int suc = 0;
        int fail = 0;
        RedisLimiter redisLimiter = new RedisLimiter();
        for (int i = 0; i < 1000; i++) {
            //RedisUtil.getInstance().isActionAllowed(userId, actionKey, period, max);
            boolean result = redisLimiter.limitWithZset(actionKey, period, max);
            if (result) {
                suc++;
            } else {
                fail++;
            }
        }

        System.out.printf("success:%s, fail:%s \n", suc, fail);
    }

    @Test

    public void testRedissonLock() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        int concurrentNum = 100;
        CyclicBarrier barrier = new CyclicBarrier(concurrentNum, new Runnable() {
            @Override
            public void run() {
                log.info("thread name:{} 开始秒杀", Thread.currentThread().getName());
            }
        });

        IntStream.range(0, concurrentNum).forEach(i -> {
            executorService.submit(() -> {
                try {
                    log.info("thread name:{} 准备秒杀", Thread.currentThread().getName());
                    barrier.await();// 等待所有任务准备就绪

                    doBusiness();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        ThreadUtil.closeExecutor(executorService);
    }

    private void doBusiness() throws InterruptedException {
        String key = "mate10";
        boolean getLock = false;
        String value = UUID.randomUUID().toString();
        try {
            getLock = RedisLuaDisLock.getLock(key, value, 1000);//redissonDisLock.tryLock(key, 50, TimeUnit.MILLISECONDS);
            if (getLock) {
                log.info("thread name:{} 秒杀成功", Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(1);
            }
        } finally {
            if (getLock) {
                //redissonDisLock.unlock(key);
                RedisLuaDisLock.releaseLock(key, value);
                log.info("thread name:{} 释放锁", Thread.currentThread().getName());
            }
        }
    }


}
