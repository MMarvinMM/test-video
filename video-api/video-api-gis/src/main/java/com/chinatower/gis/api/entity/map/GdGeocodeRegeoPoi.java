package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 逆地理编码 poi
 * @data 创建时间：2023/9/5 11:03
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdGeocodeRegeoPoi {
    /**
     * poi的id
     */
    private String id;
    /**
     * poi点名称
     */
    private String name;
    /**
     * poi类型
     */
    private String type;
    /**
     * 电话
     */
    private String tel;
    /**
     * 方向
     */
    private String direction;
    /**
     * 该POI的中心点到请求坐标的距离
     */
    private String distance;
    /**
     * poi地址信息
     */
    private String location;
    /**
     * 坐标点
     */
    private String address;
    /**
     * oi所在商圈名称
     */
    private String businessarea;
}
