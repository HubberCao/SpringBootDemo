package com.sp.service;

import com.sp.bean.model.User;

public interface UserService {

    User findByUserName(String userName);

    /**
     * 新增用户
     * @param user
     */
    void addUser(User user);
}
