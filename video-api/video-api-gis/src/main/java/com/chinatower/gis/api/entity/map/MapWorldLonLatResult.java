package com.chinatower.gis.api.entity.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @desription: 天地图经纬度查询
 * @data 创建时间：2023/8/16 10:51
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldLonLatResult {

    /**
     * 详细地址
     */
    @JsonProperty(value = "formatted_address")
    private String formattedAddress;
    /**
     * 此点的具体信息（分类）
     */
    private AddressComponent addressComponent;
}
