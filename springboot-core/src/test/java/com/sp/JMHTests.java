//package com.sp;
//
//import com.google.common.collect.Maps;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * JMH 测试
// * Created by admin on 2020/2/15.
// */
//@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
//@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
//public class JMHTests {
//
//    static class User {
//        private int id;
//        private String name;
//
//        public User(int id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//    }
//
//    static List<User> userList;
//    static {
//        userList = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            userList.add(new User(i, "user"));
//        }
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    public void testHashMapWithoutSize() {
//        Map<Integer, String> map = Maps.newHashMap();
//        for (User user : userList) {
//            map.put(user.id, user.name);
//        }
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    public void testHashMapWithSize() {
//        Map<Integer, String> map = Maps.newHashMapWithExpectedSize((int)(userList.size()/0.75f) + 1);
//        for (User user : userList) {
//            map.put(user.id, user.name);
//        }
//    }
//
//    public static void main(String[] args) throws RunnerException {
//        Options opt = new OptionsBuilder()
//                .include(JMHTests.class.getSimpleName())
//                .forks(1)
//                .build();
//
//        new Runner(opt).run();
//    }
//}
