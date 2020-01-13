package com.sp.config.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 全局异常
 */
@Getter
@Setter
public class GlobalException extends RuntimeException {

    /**
     * 错误编码
     */
    private int code;

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, int code) {
        super(message);
        this.code = code;
    }

    public GlobalException(String message, Throwable t) {
        super(message, t);
    }
}
