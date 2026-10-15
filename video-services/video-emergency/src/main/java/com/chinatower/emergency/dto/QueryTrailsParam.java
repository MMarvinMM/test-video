package com.chinatower.emergency.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author LQ
 */
@Data
public class QueryTrailsParam  {


    /**
     * userId列表
     */
    private String[] userIdList;

    /**
     * 网格员id
     */
    private String userId;


    /**
     * 开始时间
     */
    private String startTime;


    /**
     * 结束时间
     */
    private String endTime;



}
