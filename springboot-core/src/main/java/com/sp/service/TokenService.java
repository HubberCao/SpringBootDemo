package com.sp.service;

import javax.servlet.http.HttpServletRequest;

/**
 * token服务接口
 * Created by admin on 2020/2/15.
 */
public interface TokenService {

    /**
     * 创建Token
     * @return
     */
    public String createToken();

    /**
     * 校验Token
     * @param request
     * @return
     * @throws Exception
     */
    public boolean checkToken(HttpServletRequest request) throws Exception;
}
