package com.sp.bean.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 代金券
 * Created by admin on 2019/12/2.
 */
@Getter
@Setter
public class Voucher {
    private Long id;
    private Long userId;
    /**
     * 代金券类型
     */
    private String type;
    /**
     * 金额
     */
    private Double amount;
}
