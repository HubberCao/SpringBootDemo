package com.sp.dao.uid;

/**
 * 主键生成器
 * Created by admin on 2019/12/9.
 */
public interface IDGenerator {
    /**
     * 获取ID
     * @return
     */
    String getNextId();
}
