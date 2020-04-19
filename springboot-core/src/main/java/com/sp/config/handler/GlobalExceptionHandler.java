package com.sp.config.handler;

import com.sp.bean.vo.ResponseCode;
import com.sp.config.exception.GlobalException;
import com.sp.bean.vo.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理Handler
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public ResponseData jsonErrorHandler(HttpServletRequest request, GlobalException e) {
        ResponseData r = new ResponseData();
        r.setMessage(e.getMessage());
        r.setCode(e.getCode());
        //r.setData(null);
        r.setStatus(false);
        return r;
    }

    /**
     * 处理系统异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData exceptionHandler(HttpServletRequest req, Exception e){
        log.error(e.getMessage(), e);
        ResponseData r = new ResponseData();
        r.setMessage(e.getMessage());
        r.setCode(ResponseCode.SERVER_ERROR_CODE.getCode());
        //r.setData(null);
        r.setStatus(false);
        return r;
    }


}
