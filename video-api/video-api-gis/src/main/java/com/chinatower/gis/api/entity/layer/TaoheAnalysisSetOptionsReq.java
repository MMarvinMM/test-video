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
public class TaoheAnalysisSetOptionsReq extends CommonFeignEntity {
    /**
     * 最大相交数量，大于该值，查询的时候使用 union all sql；否则使用简单slq
     */
    @ApiModelProperty(value = "最大相交数量")
    private int maxIntersectsCount;

    /**
     * 分组查询时，每组的数量
     */
    @ApiModelProperty(value = "每组数量")
    private int perThreadCount;

    /**
     * 临时表的最大保存时间
     */
    @ApiModelProperty(value = "每组数量")
    private int maxSaveDay;
}