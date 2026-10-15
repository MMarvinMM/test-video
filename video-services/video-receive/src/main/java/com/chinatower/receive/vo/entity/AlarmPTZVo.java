package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: AlarmAngleVo
 * @description: 告警点ptz
 * @author: yongqi.han
 * @date: 2022/7/1
 * @requestID 需求单号及需求名称
 */
@Data
public class AlarmPTZVo implements Serializable {
    /** 告警点A的   （转义后的值） */
    private String alarmP;
    private String alarmT;
    private String alarmZ;
    /**告警点A的水平角*/
    private String flatA;
    /**告警点A的俯仰角*/
    private String flatB;
    /** 告警点A到摄像机的距离  */
    private String towerLength;

    private String xao;
    private String yao;

}
