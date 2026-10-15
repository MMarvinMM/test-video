package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BatchDeleteChannelVideoPlanVO
 * @Deacription 批量删除路学计划对象
 * @Author chenmf
 * @Date 2022/1/27 10:40
 * @Version 1.0
 **/
@Data
public class BatchDeleteChannelVideoPlanVO implements Serializable {
    
    //录像计划ids
    @NotEmpty(message = "录像计划id列表不能为空")
    private List<Long> recordPlanIdList;
    
    //设备编号
    private String deviceCode;
    
    //通道编号
    private String channelCode;
    
    //客户唯一编码
    @NotNull(message = "客户编码不能为空")
    private String tenantId;
    
    //用户编码
    private String userCode;
    
    //订购关系编码
    private String orderCode;
}
