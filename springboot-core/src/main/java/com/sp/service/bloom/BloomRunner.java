package com.sp.service.bloom;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于在项目启动时加载需要的redis相关内容
 * Created by admin on 2020/3/28.
 */

@Slf4j
@Component
public class BloomRunner implements CommandLineRunner {

    @Autowired
    private BloomService bloomService;

    @Override
    public void run(String... strings) throws Exception {
        log.info("初始化布隆过滤器");
        List<String> userList = Lists.newArrayList();
        userList.add("tom");
        for (String username : userList) {
            if (!bloomService.includeByBloomFilter("bloom", username)) {
                log.info("不存在user:", username);
                bloomService.addByBloomFilter("bloom", username);
            }
        }
    }
}
