package com.chinatower.platform.entity.order;

import lombok.Data;

/**
 * @ClassName QueryOrderVO
 * @Deacription 查询租户订购关系VO
 * @Author chenmf
 * @Date 2022/2/15 14:13
 * @Version 1.0
 **/

@Data
public class QueryOrderVO {
    
    //订单编号
    private String orderCode;
    // 订单状态
    private int orderStatus;
    
    // 摄像机编号
    private String deviceCode;
    
    // 摄像机名称
    private String deviceName;
    
    // 算法名称
    private String algorithmName;
    
    // 订购时间
    private String startDate;
    private String endDate;
    
    private String provinceId;
    private String cityId;
    
}
