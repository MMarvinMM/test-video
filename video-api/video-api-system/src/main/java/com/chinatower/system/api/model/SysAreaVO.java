package com.chinatower.system.api.model;

import lombok.Data;

import java.util.List;
/**
 * @desription: 区域
 * @data 创建时间：2023/8/22 9:48
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysAreaVO {
    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 街道
     */
    private List<String> streetCodes;

}
