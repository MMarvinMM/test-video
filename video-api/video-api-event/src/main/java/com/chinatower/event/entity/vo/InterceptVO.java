package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: InterceptVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/6/8 15:57
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class InterceptVO extends OrderInfoVO implements Serializable {

    /**
     * 行业ID
     */
    private String tradeId;

    /**
     * 行业应用版本编码
     */
    private String tradeVerCode;
    /**
     * 请求唯一标识
     */
    private String msgId;

    /**
     * 原图
     */
    private String imgUrl;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 设备厂家名称
     */
    private String modelName;

    /**
     * 时限
     */
    private String timeLimit;

    /**
     * 行业版本编码
     */
    private String applicationCode;

    /**
     * P位置
     */
    private String ptzP;

    /**
     * T位置
     */
    private String ptzT;

    /**
     * Z位置
     */
    private String ptzZ;

    /**
     * 预置位名称ID
     */
    private String presetId;

    /**
     * 专题图
     */
    private String relativePathThem;

    /**
     * 红外图
     */
    private String relativePathMir;

    private String transparentFields;
}
