package com.sp.dao.master;

import com.sp.bean.model.User;
import com.sp.config.cache.CacheableTtl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "user")
//@Mapper //可以不要
public interface UserDao {

    //@Cacheable(keyGenerator = "firstParamKeyGenerator")
    //@Cacheable(key="'user_'.concat(#a0)")
    @CacheableTtl(key="'user_'.concat(#a0)", ttl = 10)
    User findByUserName(String userName);

    void save(User user);
}
