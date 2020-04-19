package com.sp.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2020/3/23.
 */

public class LogService {


    /**
     * 日志条数统计
     */
    private volatile List<Log> logList = new LinkedList<>();

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);


    /**
     * 每10条或3 毫秒刷一次DB
     *
     * @param log
     * @return true 成功 false 失败
     */

    public boolean batchInsert(Log log) {
        try {
            if (logList.size() > 0 && logList.size() % 10 == 0) {
                doInsert();
            } else {
                logList.add(log);
            }

            scheduledThreadPool.schedule(new Runnable() {
                @Override
                public void run() {
                    doInsert();
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

    private void doInsert() {
        if (logList.size() > 0) {
            System.out.println(Thread.currentThread().getName() + " 成功入库" + logList.size() + "条数据");
            logList.clear();
        }
    }

    public class Log {

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


