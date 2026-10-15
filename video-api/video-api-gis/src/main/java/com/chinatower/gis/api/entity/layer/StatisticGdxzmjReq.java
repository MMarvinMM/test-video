package com.chinatower.gis.api.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticGdxzmjReq extends CommonFeignEntity {
    /**
     * 农田类型
     * @required false
     * */
    @ApiModelProperty(value = "农田类型")
    private String farmlandType;

}
