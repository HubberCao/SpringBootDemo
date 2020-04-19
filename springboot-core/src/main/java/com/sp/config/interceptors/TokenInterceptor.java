package com.sp.config.interceptors;

import com.sp.config.JWTConfig;
import com.sp.config.exception.GlobalException;
import com.sp.config.exception.ParamException;
import com.sp.util.HttpUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token 拦截器
 * Created by admin on 2020/2/16.
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private JWTConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 地址过滤
        String uri = request.getRequestURI();
        if (uri.contains("/login")) {
            return true;
        }
        // Token验证
        String header = jwtConfig.getHeader();
        String token = HttpUtil.getValueFromRequest(request, header);
        if (StringUtils.isBlank(token)) {
            throw new ParamException(header + " 不能为空");
        }
        Claims claims = jwtConfig.getTokenClaim(token);
        if (claims == null || jwtConfig.isTokenExpired(claims.getExpiration())) {
            throw new GlobalException(header + " 失效，请重新登录");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
