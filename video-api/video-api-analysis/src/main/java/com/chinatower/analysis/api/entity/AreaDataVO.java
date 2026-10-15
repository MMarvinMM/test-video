package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 区域公共参数VO
 */
@Data
public class AreaDataVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 区域编码
     * @required true
     */
    private String areaCode;

    /**
     * 区域名称
     * @required true
     */
    private String areaName;

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;
}
