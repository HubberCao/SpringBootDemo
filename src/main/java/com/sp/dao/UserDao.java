package com.sp.dao;

import com.sp.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "user")
@Mapper
public interface UserDao {

    @Cacheable(key="#userName")
    User findByUserName(String userName);
}
