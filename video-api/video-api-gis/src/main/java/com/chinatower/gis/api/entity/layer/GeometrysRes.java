package com.chinatower.gis.api.entity.layer;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/11/3 9:51
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeometrysRes{
    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 图班表
     */
    private String tableName;
    /**
     * 图层类型
     */
    private String geoType;
    /**
     * 坐标数据
     */
    private Object geometry;
}
