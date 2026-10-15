package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputGeoItem {
    /**
     *  请求几何的WKT形式表达，坐标系为4326
     * @required false
     * */
    @ApiModelProperty(value = " 请求几何的WKT形式表达，坐标系为4326")
    private String wkt;
    /**
     * 图形的缓冲区距离，单位米
     * @required false
     * */
    @ApiModelProperty(value = "图形的缓冲区距离，单位米")
    private double distance;
    /**
     * 标识码坐在的图层id
     * @required false
     * */
    @ApiModelProperty(value = "标识码坐在的图层id")
    private String layerID;
    /**
     * 标识码
     * @required false
     * */
    @ApiModelProperty(value = "标识码")
    private String bsm;
}
