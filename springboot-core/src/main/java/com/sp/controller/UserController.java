package com.sp.controller;

import com.sp.annotation.RepeatSubmit;
import com.sp.bean.vo.ResponseData;
//import com.sp.config.JWTConfig;
import com.sp.config.JWTConfig;
import com.sp.config.exception.GlobalException;
import com.sp.config.exception.ParamException;
import com.sp.config.exception.ServerException;
import com.sp.service.TokenService;
import com.sp.service.UserService;
import com.sp.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JWTConfig jwtConfig;

    @RepeatSubmit
    @PostMapping("/query")
    public String queryUser(HttpServletRequest request) throws InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "tom");
        data.put("age", 11);
        return "hello cors";
    }

    @GetMapping("/getToken")
    public String getToken() {
        return tokenService.createToken();
    }

    @RequestMapping("/query/{name}")
    public Object queryUserByName(@PathVariable String name) {
        /*if (name.equals("1")) {
            throw new ParamException("参数错误");
        }
        if (name.equals("2")) {
            throw new ServerException("内部错误");
        }*/
        try {
            return ResponseData.ok(userService.findByUserName(name));
        } catch (Exception e) {
            log.error("查询异常", e);
            return ResponseData.fail("查询异常");
        }
    }


    // 拦截器直接放行，返回Token, 其他方法需要Token 验证的接口
    @GetMapping("/login")
    public Map<String, String> login(String userName, String password) {
        //String userName = "tom";
        //String passWord = "123";
        Map<String, String> result = new HashMap<>();
        // 省略数据源校验
        String token = jwtConfig.getAccessToken(userName + password);
        if (StringUtils.isNoneBlank(token)) {
            result.put("token", token);
        }
        result.put("userName", userName);


        return result;
    }

    public static void main(String[] args) {
        Map<String, String> result = new HashMap<>();

        result.put("userName", "aaaa");

        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("a", result);
        System.out.println(linkedHashMap);
    }
}
