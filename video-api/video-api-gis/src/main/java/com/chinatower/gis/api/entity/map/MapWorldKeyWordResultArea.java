package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 区域
 * @data 创建时间：2023/8/22 17:52
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldKeyWordResultArea {

    /**
     * 等级
     */
    private String level;
    /**
     * 名称
     */
    private String name;
    /**
     * 范围
     */
    private String bound;
    /**
     * 编码
     */
    private String adminCode;
    /**
     * 经纬度
     */
    private String lonlat;
}
