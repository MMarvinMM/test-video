package com.chinatower.industry.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Name IndEventTrendDTO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 14 : 46
 */
@Data
public class IndEventTrendDTO extends CommonFeignEntity {

    @ApiModelProperty(value = "趋势标识", notes = "year 年、month 月、day 天")
    private String trendFlag;
    private String queryStartTime;
    private String queryEndTime;
    //省id
    private String provinceId;

    //市id
    private String cityId;

    //区县id
    private String countyId;

}
