package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageAlarmTypeVO implements Serializable {

    /**
     * 告警类型编码
     */
    private String alarmTypeCode;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 设备类型
     */
    private String deviceType;


    /**
     * 告警类型集合
     */
    private List<PageAlarmTypeVO> list;
}
