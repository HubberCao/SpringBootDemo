package com.sp.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2020/3/30.
 */
@Slf4j
public class ResourceList {
    private int count = 10000;
    public synchronized void doBusiness2() throws InterruptedException {
        String key = "mate10";

        // synchronized (this) {

        count--;
       // TimeUnit.MILLISECONDS.sleep(10);
        //log.info("thread name:{} 秒杀成功, count={}", Thread.currentThread().getName(), count);
        // }
    }

    public synchronized void say() throws InterruptedException {
        String key = "mate10";

        // synchronized (this) {
        count++;
        //System.out.println("say hello, " +  Thread.currentThread().getName() );
        //TimeUnit.MILLISECONDS.sleep(500);
        //log.info("thread name:{} 秒杀成功, count={}", Thread.currentThread().getName(), count);
        // }
    }

    public int getCount() {
        return count;
    }
}
