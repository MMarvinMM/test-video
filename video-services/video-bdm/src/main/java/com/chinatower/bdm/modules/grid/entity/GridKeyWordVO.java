package com.chinatower.bdm.modules.grid.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 地图检索返回对象
 * @Author: TangJinBo
 * @Date: 2022年 12月 14日 9:34
 */
@Data
public class GridKeyWordVO implements Serializable {

    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 名称
     */
    private String name;
    /**
     * 经纬度
     */
    private String location;
    /**
     * 所在省份名称
     */
    private String pname;
    /**
     * 所在省份code
     */
    private String pcode;
    /**
     * 城市名
     */
    private String cityname;
    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 所在区县名称
     */
    private String adname;
    /**
     * 所在区县code
     */
    private String adCode;
    /**
     * 详细地址
     */
    private String address;
}
