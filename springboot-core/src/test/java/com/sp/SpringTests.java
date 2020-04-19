package com.sp;

import com.sp.bean.model.Product;
import com.sp.bean.model.User;
import com.sp.common.contants.Constants;
import com.sp.service.ProductService;
import com.sp.service.UserService;
import com.sp.service.spring.AppConfig;
import com.sp.service.spring.ShapeGuess;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Created by admin on 2020/4/2.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
@Profile("prod")
public class SpringTests {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ShapeGuess shapeGuess;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

//    @Autowired
//    private RestTemplate restTemplate;

    @Test
    public void test() {
        System.out.println("shapeGuess=" + shapeGuess.getInitSeed() + "," + shapeGuess.getDefaultLocale());
    }

    @Test
    public void testUser() {
        /*User user = User.builder()
                .userName("Tom")
                .createTime(LocalDateTime.now())
                .money(10)
                .build();
        userService.addUser(user);*/

        User foo = userService.findByUserName("foo0");
        log.info("id={},username={}", foo.getId(), foo.getUserName());
        Product huawei = productService.findByName("huawei");
        if (huawei == null) {
            huawei = Product.builder().name("huawei").desc("m test").build();
            productService.save(huawei);
        }

        huawei = productService.findByName("huawei");
        log.info("id={},name={}, desc={}", huawei.getId(), huawei.getName(), huawei.getDesc());

        foo = userService.findByUserName("foo0");
        log.info("id={},username={}", foo.getId(), foo.getUserName());
    }

    /**
     * 测试重复提交
     */
    @Test
    public void testRepeat() {
        String url = "http://localhost:8888/user/query";
        int threadNum = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CyclicBarrier barrier = new CyclicBarrier(10, new Runnable() {
            @Override
            public void run() {
                log.info("thread name:{} 开始提交", Thread.currentThread().getName());
            }
        });

        RestTemplate restTemplate = new RestTemplate();
        IntStream.range(0, 10).forEach(i -> {
            HttpEntity request = buildRequest("userId" + i);
            executorService.submit(() -> {
                try {
                    log.info("thread name:{} 准备提交", Thread.currentThread().getName());
                    barrier.await();// 等待所有任务准备就绪

                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    log.info("thread name:{}, resp:{}", Thread.currentThread().getName(), response.getBody());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static void main(String[] args) {
        new SpringTests().testRepeat();
    }

    private HttpEntity buildRequest(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Constants.NO_REPEAT_TOKEN, "your token");
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        return new HttpEntity(body, headers);
    }
}
