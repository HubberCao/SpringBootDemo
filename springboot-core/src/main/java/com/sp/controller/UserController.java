package com.sp.controller;

import com.sp.config.exception.GlobalException;
import com.sp.service.UserService;
import com.sp.bean.vo.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//@RestController
//@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/query")
    public Object queryUser() throws GlobalException {
       Map<String, Object> data = new HashMap<>();
        data.put("name", "tom");
        data.put("age", 11);
        return ResponseData.ok(data);
    }

    @RequestMapping("/query/{name}")
    public Object queryUserByName(@PathVariable String name) throws GlobalException {
       /* if(name.equals("1")){
            throw new ParamException("参数错误");
        }
        if(name.equals("2")){
            throw new ServerException("内部错误");
        }*/
       return ResponseData.ok(userService.findByUserName(name));
    }
}
