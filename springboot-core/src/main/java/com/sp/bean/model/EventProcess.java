package com.sp.bean.model;

import com.sp.common.enums.EventProcessStatus;
import com.sp.common.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 事件处理记录
 * Created by admin on 2019/12/2.
 */
@Getter
@Setter
@ToString
public class EventProcess {
    private Long id;
    private String payload;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 事件处理状态
     */
    private EventProcessStatus status;
}
