package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 地理编码查询
 * @data 创建时间：2023/8/15 15:10
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiGeocodingQueryReq extends GisBaseReq {

    /**
    * 待解析的地址
    * @required true
    */
    private String address;
    /**
     * 地址所在的城市中文名
     * @required false
     */
    private String city;
}
