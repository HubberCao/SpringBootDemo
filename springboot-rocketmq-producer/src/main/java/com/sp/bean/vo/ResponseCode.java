package com.sp.bean.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ResponseCode {
    PARAM_ERROR_CODE(400, "参数错误"),
    SERVER_ERROR_CODE(500, "服务器内部错误");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
