package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TranslnglatVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 19:39
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TranslnglatVO implements Serializable {
    /** 错误码 */
    private String errCode;
    /**告警点A的经纬度*/
    private String longitude;
    private String latitude;

    /** 告警点A的   （转义后的值） */
    private String alarmP;
    private String alarmT;
    private String alarmZ;
    /** 告警点A的   （原始值的值） */
    private String inputP;
    private String inputT;
    private String inputZ;
    /** 告警点A的水平方位角  */
    private String flatA;
    /** 告警点A到摄像机的距离  */
    private String towerLength;

    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 摄像机水平角
     */
    private String horizontal;

}
