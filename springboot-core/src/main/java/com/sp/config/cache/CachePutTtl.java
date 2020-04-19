package com.sp.config.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cache.annotation.CachePut;
import org.springframework.core.annotation.AliasFor;

/**
 * Created by admin on 2020/4/8.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CachePut(cacheResolver = "ttlCacheResolver")
public @interface CachePutTtl {

    @AliasFor("cacheNames")
    String[] value() default {};

    @AliasFor("value")
    String[] cacheNames() default {};

    String key() default "";

    String keyGenerator() default "";

    String condition() default "";

    String unless() default "";

    long ttl();
}
