package com.chinatower.gis.api.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetXjxlFeaturesByIdsRes extends CommonFeignEntity {
    /**
     * 图斑ID数组
     * */
    @ApiModelProperty(value = "图斑ID数组")
    private List<GetXjxlFeatureByIdsRes> features;
}
