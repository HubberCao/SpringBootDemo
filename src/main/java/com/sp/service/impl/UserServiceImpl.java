package com.sp.service.impl;

import com.sp.dao.UserDao;
import com.sp.model.User;
import com.sp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User findByUserName(String userName) {
        log.info("查找用户:{}", userName);
        User u = userDao.findByUserName(userName);
        if (u == null) {
            u = User.builder().id("123").userName(userName).build();
        }
        return u;
    }
}
