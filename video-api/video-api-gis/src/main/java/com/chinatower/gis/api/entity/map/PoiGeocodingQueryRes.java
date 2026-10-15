package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 地理编码查询返回
 * @data 创建时间：2023/8/15 15:10
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiGeocodingQueryRes extends Region{
    /**
     * 查询地址
     */
    private String formattedAddress;
}
