package com.sp.bean.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class User {
    private String id;
    private String userName;
    private Date createTime;

}
