package com.sp.exception;

import com.sp.vo.ResponseCode;

/**
 * 参数异常
 */
public class ParamException extends GlobalException {

    public ParamException(String message) {
        super(message, ResponseCode.PARAM_ERROR_CODE.getCode());
    }
}
