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
public class TaoheAnalysisLayerTableReq extends CommonFeignEntity {
    /**
    * 请求的GUID
    * @required true
    */
    @ApiModelProperty(value = "请求的GUID")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private String guid;
    /**
     * 请求图层的ID
     * @required true
     * */
    @ApiModelProperty(value = "请求图层的ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
    /**
     * 起始页码
     * @required true
     * */
    @ApiModelProperty(value = "起始页码")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int firstIndex;
    /**
     * 每页行数
     * @required true
     * */
    @ApiModelProperty(value = "每页行数")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private int count;
}
