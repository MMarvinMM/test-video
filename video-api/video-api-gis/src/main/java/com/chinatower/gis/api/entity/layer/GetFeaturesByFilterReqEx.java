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
public class GetFeaturesByFilterReqEx extends GetFeaturesByFilterReq {
}
