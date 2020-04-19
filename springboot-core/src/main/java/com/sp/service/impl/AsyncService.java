package com.sp.service.impl;

import com.sp.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by admin on 2020/3/16.
 */
@Service
@Slf4j
@Transactional(value = "transactionManager", readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class AsyncService {
    //注意一定是public,且是非static方法
    @Async
    public void testSyncTask() throws InterruptedException {
        Thread.sleep(10000);
        System.out.println("异步任务执行完成！");
    }


    public void asyncCallTwo() throws InterruptedException {
        // this.testSyncTask();

        //以下才是重点!!!
        AsyncService springService = SpringBeanUtil.getApplicationContext().getBean(AsyncService.class);

        boolean isAop = AopUtils.isAopProxy(springService);//是否是代理对象；
        boolean isCglib = AopUtils.isCglibProxy(springService);  //是否是CGLIB方式的代理对象；
        boolean isJdk = AopUtils.isJdkDynamicProxy(springService);  //是否是JDK动态代理方式的代理对象；
        System.out.println(isAop + " , " + isCglib + " , " + isJdk);

        AsyncService proxy = (AsyncService) AopContext.currentProxy();
        System.out.println(springService == proxy ? true : false);
        //proxy.testSyncTask();
        springService.testSyncTask();
        System.out.println("end!!!");
    }
}
