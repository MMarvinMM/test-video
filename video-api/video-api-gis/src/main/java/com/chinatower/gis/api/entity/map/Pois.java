package com.chinatower.gis.api.entity.map;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/8/15 14:10
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class Pois {
    /**
     * POI子点的id信息
     *  @required false
     */
    private String nid;
    /**
     * POI子点名称
     *  @required false
     */
    private String name;
    /**
     * POI子点简名称
     *  @required false
     */
    private String sname;
    /**
     * POI子点类型名称，按照：大类;中类;小类 的顺序。例如：餐饮;休闲餐厅;冷饮店
     *  @required false
     */
    private String type;
    /**
     * POI子点类型码
     *  @required false
     */
    private String typecode;
    /**
     * POI子点经纬度坐标。经度在前，纬度在后，经度和纬度用“,”分割
     *  @required false
     */
    private String location;
    /**
     * POI子点地址
     *  @required false
     */
    private String address;
    /**
     * POI子点距离，单位：米；双精度，仅周边搜索有值返回
     *  @required false
     */
    private String distance;

    private List<Object> photos;
}
