package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 周边网格员搜索入参
 */
@Data
public class QueryTrailsParam extends BaseIndustryEntity {


    /**
     * 网格员id列表
     */
    private String[] keeperIdList;

    /**
     * 网格员id
     */
    private String keeperId;


    /**
     * 开始时间
     */
    private String startTime;


    /**
     * 结束时间
     */
    private String endTime;



    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataScope;



}
