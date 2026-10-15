package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFeaturesByFilterAPIReq extends GetFeaturesByFilterReq {
    /**
     * 请求图层的ID
     * */
    @ApiModelProperty(value = "请求图层的ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
    /**
     * 图层类型
     * */
    @ApiModelProperty(value = "图层类型")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private String layerIDType;
    /**
     * 字段列表
     * */
    @ApiModelProperty(value = "字段列表")
    private String cols;
    /**
     * 图形的wkt形式,要求使用4326坐标，置空为不进行空间过滤
     * */
    @ApiModelProperty(value = "图形的wkt形式,要求使用4326坐标，置空为不进行空间过滤")
    private String wktFilter;
    /**
     * 图形的wkt形式,要求使用4326坐标，置空为不进行空间过滤
     * */
    @ApiModelProperty(value = "图形的缓冲区距离")
    private double distance;

    /**
     * 属性过滤器，置空为不进行属性过滤
     * */
    @ApiModelProperty(value = "属性过滤器，置空为不进行属性过滤")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String filter;

    /**
     * 属性过滤器，置空为不进行属性过滤
     * */
    @ApiModelProperty(value = "返回字段包含Geometry")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private boolean returnGeometry;
    /**
     * 属性过滤器，置空为不进行属性过滤
     * */
    @ApiModelProperty(value = "只返回总数")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private boolean returnCountOnly;
    /**
     * 起始页码
     * */
    @ApiModelProperty(value = "起始页码")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int firstIndex;
    /**
     * 每页行数
     * */
    @ApiModelProperty(value = "每页行数")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int count;

    /**
     * 图层名称，后端查询赋值
     * */
    private String layerName;


}
