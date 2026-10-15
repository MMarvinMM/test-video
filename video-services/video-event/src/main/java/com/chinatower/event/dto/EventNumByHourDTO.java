package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName EventNumByHourDTO
 * @Deacription 获取事件数量-曲线图用
 * @Author chenmf
 * @Date 2022/6/7 14:47
 * @Version 1.0
 **/
@Data
public class EventNumByHourDTO implements Serializable {
    
    /**
     * 时段 如：0:00-1:00
     */
    private String label;
    
    /**
     * 告警数量
     */
    private String num;
}
