package com.sp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by admin on 2020/4/1.
 */
//@Configuration
//@ImportResource("classpath:prop-config.xml")
//@PropertySource("classpath:config.properties")
public class StartPlugin {
    @Value("${jdbc.url}")
    private String url;

    @PostConstruct
    public void init2() {
        int i = 0;
        System.out.println(i);
    }

    @Bean(initMethod = "init")
    public CachePlugin cachePlugin() {
        return new CachePlugin();
    }
}
