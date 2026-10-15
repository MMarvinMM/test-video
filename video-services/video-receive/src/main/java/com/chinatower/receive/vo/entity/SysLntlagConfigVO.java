package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: SysLntlagConfigVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 19:55
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SysLntlagConfigVO implements Serializable {
    private Integer id;

    /**
     * 默认P0
     */
    private String p0;

    /**
     * 默认T0
     */
    private String t0;

    /**
     * 默认Z0
     */
    private String z0;

    /**
     * 默认最小焦距
     */
    private String f0;

    /**
     * 系数
     */
    private String n;

    /**
     * 系数
     */
    private String m;

    /**
     * cmos值
     */
    private String c;
    /**
     * cmos值
     */
    private String d;
    /**
     * device
     */
    private String deviceCode;

    /**
     * 设备厂家code
     */
    private String modelId;

    /**
     * 焦距范围.
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是.
     */
    private String vertiDownNeg;

    private static final long serialVersionUID = 1L;
}