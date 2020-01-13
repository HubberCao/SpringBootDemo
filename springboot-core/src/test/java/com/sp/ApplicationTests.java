package com.sp;

import com.sp.dao.uid.IDGenerator;
import com.sp.util.RedisUtil;
import com.sp.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private IDGenerator idGenerator;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    @Test
    public void testGetId() throws IOException, InterruptedException {

        Task t1 = new Task("T1");
        Task t2 = new Task("T2");
        executorService.submit(t1);
        executorService.execute(t2);


        executorService.shutdown();

        // 每隔一秒钟检查一次线程池里的任务执行完没有，没有执行完就阻塞主线程。
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            log.info("worker running");
        }
        log.info("worker over");
        //让主线程不挂掉, 否则无法正常循环结束
        //Thread.currentThread().join();
        //System.in.read();
    }

    class Task implements Runnable {
        private String name;
        public Task(String name) {
            this.name = name;
            //Thread.currentThread().setName(name);
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);
            for (int i = 0; i < 100; i++) {
                log.info("i={}, thread={}, getid={}", i, Thread.currentThread().getName(), idGenerator.getNextId());
            }
        }
    }

    @Test
    public void contextLoads() {
       // redisTemplate.opsForValue().set("k1", "v1");
        //String k1 = redisUtils.get("k1").toString();
        String k1 = RedisUtil.getInstance().get("k1").toString();
        log.info("k1={}", k1);
    }

}
