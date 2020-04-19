package com.sp.config.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异步请求拦截
 * Created by admin on 2020/3/16.
 */
@Component
@Slf4j
public class MyAsyncHandlerInterceptor implements AsyncHandlerInterceptor {

    @Override

    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 拦截之后，重新写回数据，将原来的hello world换成如下字符串

        String resp = "my name is chhliu!";
        response.setContentLength(resp.length());
        response.getOutputStream().write(resp.getBytes());
        log.info(Thread.currentThread().getName() + " 进入afterConcurrentHandlingStarted方法");

    }

}