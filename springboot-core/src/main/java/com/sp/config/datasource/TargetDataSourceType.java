package com.sp.config.datasource;

import java.lang.annotation.*;

/**
 * 目标数据源注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSourceType {
    DataSourceType dataType() default DataSourceType.master;
}
