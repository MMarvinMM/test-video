package com.chinatower.sync.consumer.entity;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/11/9 22:36
 */
@Data
public class UavAlgorithmRequest {
    private String taskRecordId;
    private String taskId;
    //1000 算法下发成功
    //8888 算法下发失败
    //5001 算法拉流失败
    //5002 算法推流失败
    //5003 算法仓获取流地址失败
    //5004 没有可用服务
    private String taskStatus;
    private String flightRouteId;
    private String acceptNode;
}
