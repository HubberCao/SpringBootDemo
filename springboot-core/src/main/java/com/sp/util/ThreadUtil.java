package com.sp.util;

import java.util.concurrent.*;

/**
 * 多线程实现工具类
 * Created by admin on 2019/12/9.
 */
public class ThreadUtil {

    /**
     * 获取ExecutorService
     * @return
     */
    public ExecutorService getExecutorService() {
        ExecutorService executorService = new ThreadPoolExecutor(3, 5, 100, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(200), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("xxx thread");
                return t;
            }
        });

        return executorService;
    }

}
