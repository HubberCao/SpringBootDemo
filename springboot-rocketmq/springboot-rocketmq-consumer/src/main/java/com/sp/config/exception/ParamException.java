package com.sp.config.exception;

import com.sp.bean.vo.ResponseCode;

/**
 * 参数异常
 */
public class ParamException extends GlobalException {

    public ParamException(String message) {
        super(message, ResponseCode.PARAM_ERROR_CODE.getCode());
    }
}
