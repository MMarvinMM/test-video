package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeojsonStruckRes {
    /**
     * geojson 对象
     * */
    private Object geoData;
    /**
     * 查询结果GUID
     * */
    private int allCount;
    /**
     * 图层类型
     */
    private String geoType;
    /**
     * 请求图层的ID
     * */
    private String layerID;
}
