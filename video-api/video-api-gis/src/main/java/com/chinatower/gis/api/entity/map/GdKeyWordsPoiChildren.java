package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 关键字 下级
 * @data 创建时间：2023/9/5 10:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdKeyWordsPoiChildren {
    /**
     * 子poi分类编码
     */
    private String typecode;
    /**
     * 子poi详细地址
     */
    private String address;
    /**
     * 距离
     */
    private String distance;
    /**
     * 子poi所属类型
     */
    private String subtype;
    private String sname;
    /**
     * 子poi名称
     */
    private String name;
    /**
     * 子poi经纬度
     */
    private String location;
    /**
     * 子poi唯一标识
     */
    private String id;
}
