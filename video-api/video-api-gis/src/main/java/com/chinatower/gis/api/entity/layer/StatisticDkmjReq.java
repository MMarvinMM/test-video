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
public class StatisticDkmjReq extends CommonFeignEntity {
    /**
     * 图层ID
     * @required false
     * */
    @ApiModelProperty(value = "图层ID")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String layerID;
    /**
     * 标识码数组
     * @required false
     * */
    @ApiModelProperty(value = "标识码数组(对应数据表里面bsm)")
    private ArrayList<String> featureIDs;
}
