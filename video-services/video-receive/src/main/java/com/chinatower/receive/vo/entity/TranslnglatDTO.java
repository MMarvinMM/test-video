package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TranslnglatDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 19:43
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TranslnglatDTO implements Serializable {
    /** 中心点x坐标百分比 */
    private String Ux;
    /** 中心点y坐标百分比 */
    private String Vy;
    /** 摄像机P */
    private String pan;
    /** 摄像机T */
    private String tilt;
    /** 摄像机Z */
    private String zoom;
    /** 最小焦距 */
    private String f0;
    /**
     * cmos值
     */
    private String c;
    /**
     * cmos值
     */
    private String d;
    /** M */
    private String M;
    /** N */
    private String N;
    /** 高度 */
    private String h;
    /** 初始值P */
    private String P0;
    /** 初始值T */
    private String T0;

    /** 站址精度 */
    private String longitude;

    /** 站址维度 */
    private String latitude;

}