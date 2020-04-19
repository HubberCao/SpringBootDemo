package com.sp.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseData implements Serializable {
    private boolean status = true;
    private int code = 200;
    private String message;
    private Object data;

    public ResponseData() {
        super();
    }

    public ResponseData(Object data) {
        super();
        this.data = data;
    }

    public ResponseData(boolean status, int code, String message, Object data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseData(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ResponseData ok(Object data) {
        return new ResponseData(data);
    }

    public static ResponseData fail(String message) {
        return new ResponseData(false,  message);
    }

    public static ResponseData fail(String message, Object data) {
        return new ResponseData(false, ResponseCode.SERVER_ERROR_CODE.getCode(), message, data);
    }
}
