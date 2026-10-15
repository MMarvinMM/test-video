package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription:  关键字节点信息
 * @data 创建时间：2023/8/17 9:26
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldKeyWordResultPois extends Region{

    private String elemid;
    /**
     * Poi点名称
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 地址
     */
    private String address;
    /**
     * 坐标
     */
    private String lonlat;
    /**
     * poi类型
     */
    private String poiType;
    /**
     * 英文地址
     */
    private String eaddress;
    /**
     * poi点英文名称
     */
    private String ename;
    /**
     * poi热点ID
     */
    private String hotPointID;
    /**
     * 数据信息来源
     */
    private String source;
    /**
     * 1千米以下单位为米（m），1千米以上单位为千米（km）
     */
    private String distance;
    /**
     * 分类编码
     */
    private String typeCode;
    /**
     * 分类名称
     */
    private String typeName;
}
