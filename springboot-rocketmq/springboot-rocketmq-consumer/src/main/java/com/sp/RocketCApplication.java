package com.sp;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableCaching
@EnableScheduling
//@MapperScan("com.sp.dao")
public class RocketCApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketCApplication.class, args);
    }

}
