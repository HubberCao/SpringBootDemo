package com.sp.config;

import com.sp.config.interceptors.RepeatSubmitInterceptor;
import com.sp.config.interceptors.MyAsyncHandlerInterceptor;
import com.sp.config.interceptors.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by admin on 2020/1/4.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private MyAsyncHandlerInterceptor myAsyncHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor);
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
        //registry.addInterceptor(myAsyncHandlerInterceptor);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.groovy();
        registry.scriptTemplate();
    }

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;

    }

   /* @Bean
    public GroovyMarkupConfigurer groovyMarkupConfigurer() {
        GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/");
        return configurer;
    }*/

    @Bean
    public ScriptTemplateConfigurer configurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
        configurer.setEngineName("nashorn");
        configurer.setScripts("mustache.js");
        configurer.setRenderObject("Mustache");
        configurer.setRenderFunction("render");
        return configurer;
    }

    /**
     * 跨域设置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("*")
                .allowedHeaders("*")                                        //允许的请求头
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")  //允许的请求方法
                .maxAge(1800)                                               //请求的有效期
                .allowedOrigins("http://localhost:8888")                    //支持的域
                .allowCredentials(true);                                    //允许携带Cookies
    }


    /**
     * 配置异步支持
     * @param configurer
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(60*1000);// 处理callable超时
        configurer.setTaskExecutor(taskExecutor());
        configurer.registerCallableInterceptors(timeOutCallableProcessInterceptor());
    }

    /**
     * 配置线程池
     * @return
     */
    @Bean(name = "asyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("callable-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public TimeoutCallableProcessingInterceptor timeOutCallableProcessInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }
}
