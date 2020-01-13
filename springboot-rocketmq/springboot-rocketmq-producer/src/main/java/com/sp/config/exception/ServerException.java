package com.sp.config.exception;

import com.sp.bean.vo.ResponseCode;

/**
 * 服务器异常
 */
public class ServerException extends GlobalException {

    public ServerException(String message) {
        super(message, ResponseCode.SERVER_ERROR_CODE.getCode());
    }
}
