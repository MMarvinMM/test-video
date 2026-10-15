package com.chinatower.device.mongodb.vo;

import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName QuerySensorAttrValueVO
 * @Deacription
 * @Author zheng.yan
 * @Date 2022/5/10
 * @Version 1.0
 **/
@Data
@ApiModel(value = "查询传感器监测值对象",description = "查询传感器监测值对象")
public class QuerySensorAttrValueVO implements Serializable {
    
    @ApiModelProperty(value = "操作的集合类型")
    private QueryEntity queryEntity;
    
    @ApiModelProperty(value = "页数")
    private Integer pageNo = 0;
    
    @ApiModelProperty(value = "分页数")
    private Integer pageSize = 10;
    
    @ApiModelProperty(value = "排序字段")
    private SortEntity sortEntity;

}
