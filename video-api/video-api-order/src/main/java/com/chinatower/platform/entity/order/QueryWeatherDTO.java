package com.chinatower.platform.entity.order;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QueryWeatherDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/29 17:20
 * @Version 1.0
 **/
@Data
@ApiModel(description = "天气查询入参")
public class QueryWeatherDTO extends BaseIndustryEntity {

    @ApiModelProperty(value = "地市编码")
    //@NotNull(message = "地市编码不能为空")
    private String adcode;

    @ApiModelProperty(value = "查询未来3/7/10/15/30天")
    private int day;

    private String serviceName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 开始时间 格式 yyyy-MM-dd HH:mm:ss
     */
    private String startDate;

    /**
     * 结束时间 格式 yyyy-MM-dd HH:mm:ss
     */
    private String endDate;

    /**
     * 是否定时任务 true是定时任务
     */
    private Boolean taskFlag;
}
