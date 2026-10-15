package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName EventNumByAreaDTO
 * @Deacription 告警事件统计-事件高发区域
 * @Author chenmf
 * @Date 2022/6/7 14:36
 * @Version 1.0
 **/
@Data
public class EventNumByAreaDTO implements Serializable {
    
    /**
     * 省份编码
     */
    private String provinceId;
    
    /**
     * 市编码
     */
    private String cityId;
    
    /**
     * 区编码
     */
    private String countyId;
    
    /**
     * 行政区划地址
     */
    private String regionName;
    
    /**
     * 告警数量
     */
    private int num;
}
