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
public class TaoheAnalysisLayerItem {
    /**
     * 图层类型英文简拼
     * */
    private String abbreviation;
    /**
     * 请求图层的ID
     * @required true
     * */
    @ApiModelProperty(value = "请求图层的ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
}
