package com.sp;

import com.alibaba.fastjson.JSON;
import com.sp.common.contants.Constants;
import com.sp.util.ResourceList;
import com.sp.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by admin on 2020/3/30.
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws InterruptedException {
       /* ResourceList resourceList = new ResourceList();
        //ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int times = 0; times < 10; times++) {
            int threadnum = 100000;
            Thread[] threads = new Thread[threadnum];
            for (int i = 0; i < threadnum; i++) {
                Thread t = new Thread(new MyRun(resourceList, i));
                t.start();
                //t.join();
                threads[i] = t;
            }
            for (Thread t : threads) {
                t.join();
            }
            // TimeUnit.SECONDS.sleep(1);
            //Thread.currentThread().join();
            System.out.println("result=" + resourceList.getCount());
        }*/

        Test t = new Test();
        IntStream.range(0, 1).forEach(i -> {
            t.doReq();
        });
    }

    private void doReq() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter){
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });

        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:8888/user/login?userName=tom&password=123", String.class);
        String access_token = JSON.parseObject(resp.getBody()).getString("token");

        resp = restTemplate.getForEntity("http://localhost:8888/user/getToken?access_token=" + access_token, String.class);
        String no_repeat = resp.getBody();

        int threadNum = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CyclicBarrier barrier = new CyclicBarrier(threadNum, new Runnable() {
            @Override
            public void run() {
                log.info("thread name:{} 开始提交", Thread.currentThread().getName());
            }
        });

        IntStream.range(0, threadNum).forEach(i -> {
            executorService.submit(() -> {
                try {
            HttpEntity request = buildRequest("userId" + i, no_repeat, access_token, Thread.currentThread().getName());
                    log.info("thread name:{} 准备提交", Thread.currentThread().getName());
                    barrier.await();// 等待所有任务准备就绪

                    ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:8888/user/query", request, String.class);
                    log.info("thread name:{}, resp:{}, no_repeat:{}", Thread.currentThread().getName(), response.getBody(), no_repeat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        ThreadUtil.closeExecutor(executorService);
    }

    private static HttpEntity buildRequest(String userId, String no_repeat, String access_token, String reqId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.set(Constants.NO_REPEAT_TOKEN, no_repeat);
        headers.set(Constants.ACCESS_TOKEN, access_token);
        headers.set("request_id", reqId);
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        return new HttpEntity(body, headers);
    }


}

class MyRun implements Runnable {
    ResourceList resourceList;
    private  int num;

    public MyRun(ResourceList resourceList, int i) {
        this.resourceList = resourceList;
        num = i;
    }

    @Override
    public void run() {
        try {
            if (num % 2 == 0) {
                resourceList.doBusiness2();
            } else {
                resourceList.say();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void doBusiness2() throws InterruptedException {
        String key = "mate10";

        // synchronized (this) {
        System.out.println("thread name:{} 秒杀成功" + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(3);
        // }
    }
}
