package com.chinatower.gis.api.entity.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/9/5 11:00
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdGeocodeRegeo {

    /**
     * 地址元素列表
     */
    @JsonProperty(value = "formatted_address")
    private String formattedAddress;

    /**
     * 地址信息数据
     */
    private Object addressComponent;

    /**
     * poi信息列表
     */
    private List<GdGeocodeRegeoPoi> pois;
}
