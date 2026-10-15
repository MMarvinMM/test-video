package com.chinatower.gis.api.entity;


import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2024/10/16 15:36
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisSdkReq {
    /**
     * wtk 字符串
     */
    private String wkt;
    /**
     * 分段数量 默认 500
     */
    private Integer sampleNum = 500;


}
