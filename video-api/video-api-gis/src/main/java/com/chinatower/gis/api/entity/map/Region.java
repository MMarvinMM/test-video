package com.chinatower.gis.api.entity.map;

import com.chinatower.common.core.utils.StringUtils;
import lombok.Data;

/**
 * @desription: 区域
 * @data 创建时间：2023/8/15 14:05
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class Region {
    /**
     * 所属省
     *  @required true
     */
    private String province;
    /**
     * 省编码
     *  @required true
     */
    private String provinceCode;
    /**
     * 所属市
     *  @required true
     */
    private String city;
    /**
     * 市编码
     *  @required true
     */
    private String cityCode;
    /**
     * 所属区县
     *  @required false
     */
    private String county;
    /**
     * 区域编码
     *  @required false
     */
    private String countyCode;
    /**
     * 所属街道或者乡镇
     *  @required false
     */
    private String town;
    /**
     * 所属街道或者乡镇 编码
     *  @required false
     */
    private String townCode;
    /**
     * 经纬度
     *  @required false
     */
    private String location;
    /**
     * 多边形
     *  @required false
     */
    private String polygon;

    public void setProvinceCode(String provinceCode) {
        if (StringUtils.isNotEmpty(provinceCode) && provinceCode.length() >= 9){
            provinceCode = provinceCode.substring(3);
        }
        this.provinceCode = provinceCode;
    }

    public void setCityCode(String cityCode) {
        if (StringUtils.isNotEmpty(cityCode) && cityCode.length() >= 9){
            cityCode = cityCode.substring(3);
        }
        this.cityCode = cityCode;
    }

    public void setCountyCode(String countyCode) {
        if (StringUtils.isNotEmpty(countyCode) && countyCode.length() >= 9){
            countyCode = countyCode.substring(3);
        }
        if (StringUtils.isEmpty(countyCode) || "[]".equals(countyCode)){
            countyCode = null;
        }
        this.countyCode = countyCode;
    }

    public void setTownCode(String townCode) {
        if (StringUtils.isEmpty(townCode) || "[]".equals(townCode)){
            townCode = null;
        }
        this.townCode = townCode;
    }

    public void setCounty(String county) {
        if (StringUtils.isEmpty(county) || "[]".equals(county)){
            county = null;
        }
        this.county = county;
    }

    public void setTown(String town) {
        if (StringUtils.isEmpty(town) || "[]".equals(town)){
            town = null;
        }
        this.town = town;
    }
}
