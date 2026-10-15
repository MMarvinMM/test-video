package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlarmTypeReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 事件编码
     */
    private String eventId;

    /**
     * 告警类型数据
     */
    private List<AlarmTypeDataReq> data;
}
