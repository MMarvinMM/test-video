package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetXlsjFeaturesReq extends CommonFeignEntity {
    /**
     * 请求图层的ID
     * @required false
     * */
    @ApiModelProperty(value = "请求图层的ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
    /**
     * 属性过滤器，置空为不进行属性过滤
     * @required false
     * */
    @ApiModelProperty(value = "返回字段包含Geometry")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private boolean returnGeometry;

    /**
    * 图斑编号
    * @required false
    */
    @ApiModelProperty(value = "图斑编号")
    private String tbbh;

    /**
     * 起始页码
     * @required false
     * */
    @ApiModelProperty(value = "起始页码")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int firstIndex;
    /**
     * 每页行数
     * @required false
     * */
    @ApiModelProperty(value = "每页行数")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int count;
}
