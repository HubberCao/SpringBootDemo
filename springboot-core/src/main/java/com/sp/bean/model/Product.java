package com.sp.bean.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2020/4/3.
 */
@Getter
@Setter
@Builder
public class Product {
    private long id;
    private String name;
    private String desc;
}
