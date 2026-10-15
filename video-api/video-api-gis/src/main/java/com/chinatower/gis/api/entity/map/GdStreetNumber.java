package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 高德街道
 * @data 创建时间：2023/9/5 11:23
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdStreetNumber {
    private String number;
    private String location;
    private String direction;
    private String distance;
    private String street;
}
