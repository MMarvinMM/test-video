package com.chinatower.gis.api.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetXjxlFeatureByIdsRes extends CommonFeignEntity {
    /**
     * 查询结果GUID
     * */
    private String bsm;
    /**
     * 图斑名称
     * */
    private String name;
    /**
     * 坐落单位名称
     * */
    private String zldwmc;
    /**
     * 地类名称
     * */
    private String dlmc;
    /**
     * 几何实体
     * */
    private String geometry;
}
