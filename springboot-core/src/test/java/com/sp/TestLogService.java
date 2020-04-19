package com.sp;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 需求描述
 * 业务逻辑：每10条或3 毫秒刷一次DB，并打印写入数量
 * 测试打印日志
 *  1. 线程调用日志
 *  2. 总的 qps
 */
public class TestLogService {
    public static void main(String[] args) throws InterruptedException {
        /**
         * 每次用多少线程压测
         * 80, 400,
         */
        int threadCount = 80;

        long start = System.currentTimeMillis();
        test(threadCount, new LogService());
        long end = System.currentTimeMillis();
        long sec = (end-start)/1000;
        if (sec == 0) {
            sec = 1;
        }
        System.out.println("总共耗时：" + sec + "秒， qps：" + threadCount/sec);
        System.exit(1);
    }

    /**
     * 同时并发线程测试
     * 打印日志
     *  1. 线程调用日志
     *  2. qps
     * @param threadCount
     * @param logService
     */
    public static void test(int threadCount, LogService logService) {
        ExecutorService executor = getExecutorService(threadCount);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                        System.out.println(Thread.currentThread().getName() + "正处理");
                        LogService.Log log = logService.new Log(UUID.randomUUID().toString(), "test");
                        logService.batchInsert(log);
                        // 等待所有任务准备就绪
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //处理完成后关闭线程池
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cyclicBarrier.reset();
    }


    /**
     * 获取ExecutorService
     *
     * @return
     */
    public static ExecutorService getExecutorService(int threadCount) {
        ExecutorService executorService = new ThreadPoolExecutor(threadCount, threadCount, 100, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100));
        return executorService;
    }

    static class LogService {

        /**
         * 日志条数统计
         */
        private List<LogService.Log> logList = new LinkedList<>();

        private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

        /**
         * 每10条或3 毫秒刷一次DB
         *
         * @param log
         * @return true 成功 false 失败
         */
        public synchronized boolean batchInsert(LogService.Log log) {
            try {
                if (logList.size() > 0 && logList.size() % 10 == 0) {
                    doInsert(2);
                } else {
                    logList.add(log);
                }

                scheduledThreadPool.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {

                        doInsert(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3, TimeUnit.MILLISECONDS);
                return true;
            } catch (Exception e) {
                // 打印日常栈
                System.out.println("批量写入异常");
                e.printStackTrace();
                return false;
            }

        }

        private void doInsert(int i) {
            if (logList.size() > 0) {
                /*if  (i == 1) {
                    String s = null;
                    System.out.println(s.toString());
                }*/
                System.out.println(Thread.currentThread().getName() + " 成功入库" + logList.size() + "条数据");
                logList.clear();
            }
        }

        class Log {
            /**
             * 日志ID
             */
            String id;
            /**
             * 日志内容
             */
            String logContent;

            public Log(String id, String logContent) {
                this.id = id;
                this.logContent = logContent;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLogContent() {
                return logContent;
            }

            public void setLogContent(String logContent) {
                this.logContent = logContent;
            }
        }
    }

}
