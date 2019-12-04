package com.sp.bean.model;

import com.sp.common.enums.EventPublishStatus;
import com.sp.common.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 事件发布记录
 * Created by admin on 2019/12/2.
 */
@Getter
@Setter
@ToString
public class EventPublish {
    private Long id;
    /**
     *  事件内容，保存发送到消息队列的json字符串
	 * payload单词含义：有效载荷，在计算机中代表一个数据包或者其它传输单元中运载的基本必要数据
	 */
    private String payload;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 事件发布状态
     */
    private EventPublishStatus status;
}
