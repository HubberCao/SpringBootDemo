package com.sp.service.impl;

import com.sp.common.contants.Constants;
import com.sp.config.exception.GlobalException;
import com.sp.config.exception.ParamException;
import com.sp.config.exception.ServerException;
import com.sp.config.interceptors.RepeatSubmitInterceptor;
import com.sp.service.TokenService;
import com.sp.util.HttpUtil;
import com.sp.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


/**
 * Token实现类
 * Created by admin on 2020/2/15.
 */

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String createToken() {
        String token = StringUtils.join(Constants.Redis.TOKEN_PREFIX, UUID.randomUUID().toString());
        if(redisUtils.set(token, token, 1)) {
            return token;
        }
        return null;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) throws Exception {
        String token = HttpUtil.getValueFromRequest(request, Constants.NO_REPEAT_TOKEN);
        if (StringUtils.isBlank(token)) {
            throw new ParamException("获取参数token为空");
        }

        if (!redisUtils.hasKey(token)) {
            throw new GlobalException("重复操作");
        }

        boolean remove = redisUtils.del(token);
        if (!remove) {
            throw new ServerException("请稍后再试！");
        }
        return true;
    }


}


