package com.chinatower.platform.entity.order;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName WeatherHsitoryVO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/3 19:40
 * @Version 1.0
 **/
@Data
@ApiModel(value = "查询历史天气对象",description = "查询历史天气对象")
public class QueryWeatherHsitoryVO extends BaseIndustryEntity {

    @ApiModelProperty(value = "操作的集合类型")
    private QueryEntity queryEntity;

    @ApiModelProperty(value = "页数")
    private Integer pageNo = 0;

    @ApiModelProperty(value = "分页数")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private SortEntity sortEntity;

}
