package com.sp.controller;

import com.sp.service.impl.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2020/3/16.
 */
//@RestController
//@RequestMapping("/spring")
@Slf4j
public class AsyncController {
    @Autowired
    private AsyncService springService;

    public static ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(30);

    @Resource(name = "asyncPoolTaskExecutor")
    private Executor executor;

    // 异步调用测试
    @GetMapping("/async")
    public Map<String, String> async(String userName, String password) throws InterruptedException {
        Map<String, String> result = new HashMap<>();
        result.put("aaa", "bbb");
        /*this.testAsyncTask();
        System.out.println("..." + Thread.currentThread().getName());
        *//*UserController uc = SpringBeanUtil.getApplicationContext().getBean(UserController.class);
        uc.testAsyncTask();*/
        springService.asyncCallTwo();
        return result;
    }

    @Async
    public void testAsyncTask() throws InterruptedException {
        System.out.println("in..." + Thread.currentThread().getName());
        Thread.sleep(10000);
        System.out.println("test...");
    }



    /////////////异步请求//////////////////
    @RequestMapping("/servletAsync")
    public void todoAsync(HttpServletRequest request,
                          HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                log.info("超时了：");
                //做一些超时后的相关操作
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                // TODO Auto-generated method stub
                log.info("线程开始");
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                log.info("发生错误：",event.getThrowable());
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                log.info("执行完成");
                //这里可以做一些清理资源的操作

            }
        });
        //设置超时时间
        asyncContext.setTimeout(100);
        //也可以不使用start 进行异步调用
       executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                    log.info("内部线程：" + Thread.currentThread().getName());
                    asyncContext.getResponse().setCharacterEncoding("utf-8");
                    asyncContext.getResponse().setContentType("text/html;charset=UTF-8");
                    asyncContext.getResponse().getWriter().println("这是【异步】的请求返回");
                } catch (Exception e) {
                    log.error("异常：",e);
                }
                //异步请求完成通知
                //此时整个请求才完成
                //其实可以利用此特性 进行多条消息的推送 把连接挂起。。
                asyncContext.complete();
            }
        });

        /*asyncContext.start(new Runnable() {
            @Override
            public void run() {
                //...这样不会调用自定义的线程池
            }
        });*/
        //此时之类 request的线程连接已经释放了
        log.info("线程：" + Thread.currentThread().getName());
    }

    @RequestMapping("/callable")
    public Callable<String> callable() {
        log.info("外部线程：" + Thread.currentThread().getName());
        return new Callable<String>() {

            @Override
            public String call() throws Exception {
                log.info("内部线程：" + Thread.currentThread().getName());
                return "callable!";
            }
        };
    }

    @RequestMapping("/webAsyncTask")
    public WebAsyncTask<String> webAsyncTask() {
        log.info("外部线程：" + Thread.currentThread().getName());
        WebAsyncTask<String> result = new WebAsyncTask<String>(60*1000L, new Callable<String>() {

            @Override
            public String call() throws Exception {
                log.info("内部线程：" + Thread.currentThread().getName());
                return "WebAsyncTask!!!";
            }
        });
        result.onTimeout(new Callable<String>() {

            @Override
            public String call() throws Exception {
                // TODO Auto-generated method stub
                return "WebAsyncTask超时!!!";
            }
        });
        result.onCompletion(new Runnable() {

            @Override
            public void run() {
                //超时后 也会执行此方法
                log.info("WebAsyncTask执行结束");
            }
        });
        return result;
    }

    @GetMapping("/deferredResultReq")
    public DeferredResult<String> deferredResultReq() {
        System.out.println("外部线程：" + Thread.currentThread().getName());
        //设置超时时间
        DeferredResult<String> result = new DeferredResult<String>(60*1000L);
        //处理超时事件 采用委托机制
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                System.out.println("DeferredResult超时");
                result.setResult("超时了!");
            }
        });
        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                //完成后
                System.out.println("调用完成");
            }
        });
        executor.execute(new Runnable() {

            @Override
            public void run() {
                //处理业务逻辑
                System.out.println("内部线程：" + Thread.currentThread().getName());
                //返回结果
                result.setResult("DeferredResult!!");
            }
        });
        return result;
    }

}
