package com.chinatower.gis.api.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetInOutBalanceReq extends CommonFeignEntity {
    /**
     * 耕地的进出平衡计算结果的GUID
     * */
    @ApiModelProperty(value = "耕地的进出平衡计算结果的GUID")
    private String guid;
}