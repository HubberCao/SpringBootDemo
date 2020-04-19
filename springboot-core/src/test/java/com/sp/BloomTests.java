//package com.sp;
//
//import com.sp.service.bloom.BloomLuaService;
//import com.sp.service.bloom.BloomService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.stream.IntStream;
//
///**
// * Created by admin on 2020/3/28.
// */
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@Slf4j
//public class BloomTests {
//    @Autowired
//    private BloomService bloomService;
//    @Test
//    public void test() {
//        boolean exists = bloomService.includeByBloomFilter("bloom", "tom");
//        System.out.println(exists);
//    }
//
//    @Test
//    public void testlua() {
//        String key = "user:";
//        IntStream.range(10000, 10010).forEach(i -> {
//            String value = String.valueOf("user-" + i);
//            //RedisUtil.getInstance().addBloomFilter(key, value);
//            boolean exists = BloomLuaService.includeByBloomFilter(key, value);
//            if (exists) {
//                System.out.println(value);
//            } else
//                System.out.println(value);
//        });
//    }
//}
