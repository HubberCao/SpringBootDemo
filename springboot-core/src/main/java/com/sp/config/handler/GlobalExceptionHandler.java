package com.sp.config.handler;

import com.sp.config.exception.GlobalException;
import com.sp.bean.vo.ResponseData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理Handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public ResponseData jsonErrorHandler(HttpServletRequest request, GlobalException e) throws Exception{
        ResponseData r = new ResponseData();
        r.setMessage(e.getMessage());
        r.setCode(e.getCode());
        //r.setData(null);
        r.setStatus(false);
        return r;
    }
}
