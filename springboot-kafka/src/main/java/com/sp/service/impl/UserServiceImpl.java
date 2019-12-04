package com.sp.service.impl;

import com.google.gson.Gson;
import com.sp.dao.UserDao;
import com.sp.bean.model.User;
import com.sp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private EventPublishDao eventPublishDao;

    @Override
    public User findByUserName(String userName) {
        log.info("查找用户:{}", userName);
        User u = userDao.findByUserName(userName);
        if (u == null) {
            u = User.builder().id("123").userName(userName).build();
        }
        return u;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(User user) {
        userDao.save(user);

        EventPublish eventPublish = new EventPublish();
        eventPublish.setEventType(EventType.USER_CREATED);
        eventPublish.setPayload(new Gson().toJson(user));
        eventPublish.setStatus(EventPublishStatus.NEW);
        eventPublishDao.save(eventPublish);
    }
}
