package com.sp.service;

import com.sp.bean.model.User;

public interface UserService {

    User findByUserName(String userName);
}
