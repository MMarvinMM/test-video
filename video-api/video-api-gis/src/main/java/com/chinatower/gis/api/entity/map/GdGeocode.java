package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 高德逆地理编码
 * @data 创建时间：2023/9/5 10:59
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdGeocode {
    /**
     * 结果状态值，值为0或1 0：请求失败；1：请求成功
     */
    private Integer status;
    /**
     * status为0时，info返回错误原因，否则返回“OK”
     */
    private String info;

    private GdGeocodeRegeo regeocode;
}
