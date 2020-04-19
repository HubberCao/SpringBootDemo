package com.sp.config.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2020/4/4.
 */
@Aspect
//@Order(-10) // 保证该AOP在@Transactional之前执行
@Component
@Slf4j
public class DataSourceAspect {

    @Pointcut(value = "execution (public * com.sp.dao.*(..))")
    public void webLog(){}

    //@Before("execution (public * com.sp.dao.second.ProductDao.*(..))")
    //@Before("execution (* com.sp..dao.*.*(..))")
    @Before("@annotation(targetDataSourceType)")
    public void setDataSourceKey(JoinPoint point, TargetDataSourceType targetDataSourceType) {
        //获取类法上的注解
        /*TargetDataSourceType targetDataSourceType = null;
        for (Class<?> cls : point.getSignature().getClass().getInterfaces()) {
            targetDataSourceType = cls.getAnnotation(TargetDataSourceType.class);
            if (targetDataSourceType != null) {
                DataSourceType dataSourceType = targetDataSourceType.dataType();
                if (DatabaseContextHolder.getDatabaseType() == null || !DatabaseContextHolder.getDatabaseType().equals(dataSourceType)) {
                    System.out.println("UseDataSource : {} > {}" + dataSourceType.name() + point.getSignature());
                    DatabaseContextHolder.setDataSourceType(dataSourceType);
                }
                break;
            }
        }*/
        DataSourceType dataSourceType = targetDataSourceType.dataType();
        if (DatabaseContextHolder.getDatabaseType() == null || !DatabaseContextHolder.getDatabaseType().equals(dataSourceType)) {
            log.info("设置数据源 : {} > {}", dataSourceType.name(), point.getSignature());
            DatabaseContextHolder.setDataSourceType(dataSourceType);
        }
    }

    @After("@annotation(targetDataSourceType)")
    public void clearDataSourceKey(JoinPoint point, TargetDataSourceType targetDataSourceType) {
        DataSourceType dataSourceType = targetDataSourceType.dataType();
        log.info("释放Threadlocal : {} > {}", dataSourceType.name(), point.getSignature());
        DatabaseContextHolder.clearDataSourceType();
    }
}
