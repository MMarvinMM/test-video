package com.chinatower.gis.api.entity;

import lombok.Data;

import java.util.List;

/**
 * @desription: 海拔查询
 * @data 创建时间：2024/1/12 10:28
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class AltitudesReq {

    /**
     * 经纬度 逗号分割
     */
    private List<LonLat> lonLat;
}
