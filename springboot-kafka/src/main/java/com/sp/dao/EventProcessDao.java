package com.sp.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created by admin on 2019/12/2.
 */
@Mapper
public interface EventProcessDao {
    void save(EventProcess eventProcess);
}
