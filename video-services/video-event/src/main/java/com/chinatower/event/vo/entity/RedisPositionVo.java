package com.chinatower.event.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: sxf
 * @Description:
 * 告警接收Redis记录实体
 */
@Data
public class RedisPositionVo implements Serializable {

    /** 经度 */
    private String longitude;
    /** 纬度 */
    private String latitude;
    /**
     * 告警时间
     */
    private String alarmTime;

}
