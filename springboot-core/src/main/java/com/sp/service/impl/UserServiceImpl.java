package com.sp.service.impl;

import com.sp.bean.model.User;
import com.sp.dao.master.UserDao;
import com.sp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User findByUserName(String userName) {
        log.info("查找用户2:{}", userName);
        User u = userDao.findByUserName(userName);
        if (u == null) {
            u = User.builder().id("123").userName(userName).build();
        }
        return u;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        log.info("保存用户...");
        userDao.save(user);
        String s = null;
        log.info(s.toLowerCase());
    }


}
