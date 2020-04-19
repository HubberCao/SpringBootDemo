package com.sp.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Redis实现限流
 * Created by admin on 2020/2/14.
 */


@Slf4j
public class RedisLimiter {

    private RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringBeanUtil.getBean("redisTemplate");

    public static void main(String[] args) {
        RedisLimiter limiter = new RedisLimiter();
        for (int i = 0; i < 20; i++) {
            //每个用户在1秒内最多能做五次动作
            //System.out.println(limiter.isActionAllowed("viscu","reply",1,5));
        }
    }

   /**
     *
     * 查看用户操作是否允许,
     * 参考 Redis 深度历险：核心原理与应用实践
     * https://www.cnblogs.com/jinlin/p/10300888.html
     * https://www.jianshu.com/p/95f7a165b29b
     * https://www.jianshu.com/p/f060a8767365
     * @param userId 用户Id
     * @param actionKey 行为名
     * @param period 时间范围, 单位秒
     * @param maxCount 最大次数
     * @return 是否允许
     */

    /*public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key=String.format("hits:%s:%s",userId,actionKey);
        long nowTs=System.currentTimeMillis();
        //毫秒时间戳
        Pipeline pipeline=jedis.pipelined();
        pipeline.multi();//用了multi，也就是事务，能保证一系列指令的原子顺序执行
        //value和score都使用毫秒时间戳
        pipeline.zadd(key,nowTs,nowTs+"");
        //移除时间窗口之前的行为记录，剩下的都是时间窗口内的
        pipeline.zremrangeByScore(key,0,nowTs-period*1000);
        //获得[nowTs-period*1000,nowTs]的key数量
        Response<Long> count=pipeline.zcard(key);
        //每次设置都能保持更新key的过期时间
        pipeline.expire(key,period);
        pipeline.exec();
        pipeline.close();
        return count.get()<=maxCount;


        return false;
    }*/


    public boolean isActionAllowed2(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hits:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        List<Object> objects = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();
                connection.zAdd(key.getBytes(), nowTs, String.valueOf(nowTs).getBytes());
                connection.zRemRangeByScore(key.getBytes(), 0, nowTs - period * 1000);
                Long count = connection.zCard(key.getBytes());
                connection.expire(key.getBytes(), period);
                return count;
            }
        });
        System.out.println(objects.toString());
        return false;
    }

    public boolean limitWithZset(String redisKey, Integer seconds, Integer times) {
        boolean result = false;
        //定义redis操作
        Long endTime = System.currentTimeMillis() / 1000;
        //设置最后一次发送
        Long startTime = endTime - seconds;
        //移出之前已无效的记录
        //当前redis中有效总条数
        Long count = RedisUtil.getInstance().zSetCount(redisKey, startTime, endTime);
        //如果条数大于或等于条数限制，则抛出异常，发送太多次
        if (count >= times) {
            log.info("规定时间：{}秒内，请求次数：{}过多", seconds, count);
            return result;
        }
        String value = UUID.randomUUID().toString().replaceAll("-", "");
        //向set中添加新记录
        result = RedisUtil.getInstance().zSetAdd(redisKey, value, endTime);
        if (!result) {
            log.info("add redis set error, key:{}, score:{}", redisKey, endTime);
            return result;
        }
        //当前有效的总条数
        count = RedisUtil.getInstance().zSetCount(redisKey, startTime, endTime);
        if (count >= times) {
            RedisUtil.getInstance().zSetRemoveRangeByScore(redisKey, 0, startTime);
            System.out.println("here1............count=" + count);
            Long rank = RedisUtil.getInstance().zSetRank(redisKey, value);
            if (rank < times) {
                System.out.println("here2............, rank=" + rank);
                return true;
            } else {
                RedisUtil.getInstance().zSetRemove(redisKey, value);
                System.out.println("here3............, rank=" + rank);
                return false;
            }
        }
        return result;
    }
}
