package com.sp.service;

import com.sp.model.User;

public interface UserService {

    User findByUserName(String userName);
}
