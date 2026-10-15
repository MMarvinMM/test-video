package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import lombok.Data;

/**
 * @desription: 轨迹查询
 * @data 创建时间：2023/8/15 15:27
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TrajectoryQueryReq extends GisBaseReq {

    /**
    * 起点经纬度
    * @required true
    */
    private String origin;

    /**
     * 终点经纬度
     * @required true
     */
    private String end;
}
