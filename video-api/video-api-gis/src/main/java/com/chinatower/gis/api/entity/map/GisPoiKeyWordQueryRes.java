package com.chinatower.gis.api.entity.map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: POI-关键字搜索接口 返回
 * @data 创建时间：2023/8/15 11:30
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisPoiKeyWordQueryRes {
    /**
     * 别名
     *  @required false
     */
    private String alias;
    /**
     *POI热度
     *  @required false
     */
    private String hit;
    /**
     *品牌代码
     *  @required false
     */
    private String brandCode;
    /**
     *品牌名称
     *  @required false
     */
    private String brand;
    /**
     *标签
     *  @required false
     */
    private String tag;
    /**
     * 单位：米；双精度，仅周边搜索有值返回
     *  @required false
     */
    private String distance;
    /**
     * 区域
     *  @required false
     */
    private Region region;
    /**
     * 线
     *  @required false
     */
    private List<Pois> pois;

}
