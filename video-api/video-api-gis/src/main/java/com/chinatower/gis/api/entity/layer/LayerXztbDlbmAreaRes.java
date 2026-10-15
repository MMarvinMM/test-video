package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 耕地类型分类面积统计
 * @data 创建时间：2024/6/4 15:03
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerXztbDlbmAreaRes {
    //polygon_xztb_1713509181429_37l8pg
    //DLBM（地类编码）为0101水田,DLBM（地类编码）为0103旱地,DLBM（地类编码）为0102水浇地
    /**
     * 名称
     */
    private String codeName;
    /**
     * 值
     */
    private String codeValue;
    /**
     * 统计的面积 平方米
     */
    private String area;
}
