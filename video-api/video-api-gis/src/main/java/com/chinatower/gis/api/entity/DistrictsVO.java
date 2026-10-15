package com.chinatower.gis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictsVO {
    /**
     * 地市
     * */
    private String citycode;
    /**
     * 区域
     * */
    private String adcode;
    /**
     * 级别
     * */
    private String level;
    /**
     * 中心点
     * */
    private String center;
    /**
     * 名称
     * */
    private String name;
    /**
     * 下级行政区列表
     * */
    private String[] districts;
    /**
     * 行政区边界坐标点
     * */
    private String polyline;


    /**
     * 所属省
     * */
    private String province;

    /**
     * 省编码
     * */
    private String provinceCode;

    /**
     * 所属市
     * */
    private String city;


    /**
     * 所属区县
     * */
    private String county;

    /**
     * 区县编码
     * */
    private String countyCode;

    /**
     * 所属街道或者乡镇
     * */
    private String town;

    /**
     * 乡镇编码
     * */
    private String townCode;

}
