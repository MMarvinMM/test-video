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
public class SearchXZTBReq extends GetFeaturesByFilterReqEx {
    /**
     * 地类名称
     * @required false
     * */
    @ApiModelProperty(value = "地类名称")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private String dlmc;
    /**
     * 坐落单位名称
     * */
    @ApiModelProperty(value = "坐落单位名称")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String zldwmc;
    /**
     * 图斑编号
     * */
    @ApiModelProperty(value = "图斑编号")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String tbbh;

    /**
     * 耕地坡度级别
     * */
    @ApiModelProperty(value = "耕地坡度级别")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private List<String> gdpdj;
}
