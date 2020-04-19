package com.sp.config.interceptors;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * 事务拦截
 * 注解：@Configuration/@Component，事务都生效
 */
// @Configuration
public class TxAdviceInterceptor3 {

    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.sp..service.*.*(..))";

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 获取事务配置
     *
     * @return
     */
    public Properties getAttrubites() {
        Properties attributes = new Properties();
        //查询
        attributes.setProperty("get*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        attributes.setProperty("query*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        attributes.setProperty("find*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        attributes.setProperty("select*", "PROPAGATION_REQUIRED,-Throwable,readOnly");

        //添加
        attributes.setProperty("add*", "PROPAGATION_REQUIRED,-Exception");
        attributes.setProperty("insert*", "PROPAGATION_REQUIRED,-Exception");
        attributes.setProperty("save*", "PROPAGATION_REQUIRED,-Exception");
        attributes.setProperty("create*", "PROPAGATION_REQUIRED,-Exception");

        //更新
        attributes.setProperty("update*", "PROPAGATION_REQUIRED,-Exception");
        attributes.setProperty("modify*", "PROPAGATION_REQUIRED,-Exception");

        //删除
        attributes.setProperty("delete*", "PROPAGATION_REQUIRED,-Exception");
        attributes.setProperty("remove*", "PROPAGATION_REQUIRED,-Exception");
        return attributes;

    }

    ////////////////////////方式1//////////////////////////////////
    /**
     * 3.
     * @return
     */
   /* @Bean(name = "txAdvice")
    public TransactionInterceptor txAdvice() {
        TransactionInterceptor txInterceptor = new TransactionInterceptor();
        txInterceptor.setTransactionManager(transactionManager);
        txInterceptor.setTransactionAttributes(getAttrubites());
        return txInterceptor;
    }

    @Bean
    public BeanNameAutoProxyCreator txProxy() {
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setInterceptorNames("txAdvice");
        creator.setBeanNames("*Service", "*ServiceImpl");
        creator.setProxyTargetClass(true);
        return creator;
    }*/
    ////////////////////////方式1 END//////////////////////////////////

    ////////////////////////方式2//////////////////////////////////
    /**
     * 采用注解实例化的拦截器Bean，注入切面配置信息
     * 以下 3、4为变体写法
     */
    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(TransactionInterceptor ti){
        ti.setTransactionManager(transactionManager);
        ti.setTransactionAttributes(getAttrubites());
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, ti);
    }
    ////////////////////////方式2 END//////////////////////////////////

    ////////////////////////方式3//////////////////////////////////
    /**
     * 用户自定义事务拦截器
     */
   /* @Bean
    public TransactionInterceptor txAdvice() {
        TransactionInterceptor txInterceptor = new TransactionInterceptor();
        txInterceptor.setTransactionManager(transactionManager);
        txInterceptor.setTransactionAttributes(getAttrubites());
        return txInterceptor;
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }*/
    ////////////////////////方式3 END//////////////////////////////////

    ////////////////////////方式4 END//////////////////////////////////
    /*@Bean
    public TransactionInterceptor txAdvice() {
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, getAttrubites());
        return txAdvice;
    }

    @Bean
    public AspectJExpressionPointcut aspectJExpressionPointcut(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(){
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(aspectJExpressionPointcut());
        advisor.setAdvice(txAdvice());
        return advisor;
    }*/
    ////////////////////////方式4 END//////////////////////////////////
}
