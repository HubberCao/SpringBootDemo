package com.sp.vo;

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

    public static ResponseData ok(Object data) {
        return new ResponseData(data);
    }
}
