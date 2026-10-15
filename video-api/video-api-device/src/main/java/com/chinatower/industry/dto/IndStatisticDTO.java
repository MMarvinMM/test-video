package com.chinatower.industry.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Name IndStatisticDTO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 11 : 02
 */
@Data
public class IndStatisticDTO extends CommonFeignEntity {

    @ApiModelProperty(value = "维度标识", notes = "1当年、2当月、3前一天、4当天")
    private String dimensionFlag;

    private String queryStartTime;
    private String queryEndTime;
    //省id
    private String provinceId;

    //市id
    private String cityId;

    //区县id
    private String countyId;
}
