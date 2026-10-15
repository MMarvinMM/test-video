package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetXlsjFeaturesRes {
    /**
     * 五种类型统计分析结果
     * */
    private List<GetXlsjFeatureRes> features;
    /**
     * 查询结果GUID
     * */
    private int allCount;
}
