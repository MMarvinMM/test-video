package com.chinatower.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName QueryRecordPlanCheckDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/13 19:55
 * @Version 1.0
 **/
@Data
@ApiModel(description = "查询通道录像计划校验入参")
public class QueryRecordPlanCheckDTO implements Serializable {
    
    //客户唯一编码
    @NotNull(message = "客户编码不能为空")
    @ApiModelProperty(value = "客户编码")
    private String tenantId;
    
    //存储时长 录像存储的最长周期 单位:天
    @Min(value=1, message = "存储时长需大于等于1天！")
    @ApiModelProperty(value = "存储时长需大于等于1天")
    private int storageDay;
    
    @Valid
    private List<ChekcChannelVideoPlanVO> channelVideoPlanVOList;


    private String industryCode;
}
