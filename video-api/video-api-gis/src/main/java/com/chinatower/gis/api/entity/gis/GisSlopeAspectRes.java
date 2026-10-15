package com.chinatower.gis.api.entity.gis;

import lombok.Data;

/**
 * @desription: 坡度坡向返回
 * @data 创建时间：2024/6/6 11:11
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisSlopeAspectRes {

    /**
     * 坡度 单位：%
     */
    private Double slope;
    /**
     * 坡向 阳坡(坡向向南,包括135～225°)、阴坡(坡向向北,包括315～45°)、半阳坡(坡向向东南或西南偏南,包括90～135°和225～270°)、半阴坡(坡向西北或东北偏北,包括45～90°和270～315°)
     */
    private Double aspect;
}
