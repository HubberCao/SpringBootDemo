package com.sp.dao;

import com.sp.bean.model.EventPublish;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2019/12/2.
 */
//@Mapper
public interface EventPublishDao {
    void save(EventPublish eventPublish);

    List<EventPublish> list(Map<String, Object> param);

   // void updateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
}
