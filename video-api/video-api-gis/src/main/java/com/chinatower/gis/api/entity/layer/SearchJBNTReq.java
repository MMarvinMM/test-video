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
public class SearchJBNTReq extends GetFeaturesByFilterReqEx {
    /**
     * 图斑编号
     * @required false
     * */
    @ApiModelProperty(value = "图斑编号")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private String tbbh;

    /**
     * 地类名称
     * @required false
     * */
    @ApiModelProperty(value = "地类名称")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> dlmc;

    /**
     * 耕地类型
     * @required false
     * */
    @ApiModelProperty(value = "耕地类型")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> gdlx;
    /**
     * 坡度级别
     * @required false
     * */
    @ApiModelProperty(value = "耕地坡度级别")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> gdpdjb;
    /**
     * 种植属性
     * @required false
     * */
    @ApiModelProperty(value = "种植属性名称 ")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> gdzzsxmc;
    /**
     * 耕地质量等级
     * @required false
     * */
    @ApiModelProperty(value = "耕地质量等级")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> gddj;
}
