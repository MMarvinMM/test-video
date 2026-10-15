package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 广播子平台
 * 4.35.广播任务状态推送(MQ推送)实体类
 * @author huang.xiangyu
 */
@Data
public class HornTaskStatusReq implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     * 1, 等待播发
     * 2, 播发中
     * 3, 播发成功
     * 4, 播发失败
     * 5, 播发取消
     */
    private Integer status;

    /**
     * 上报时间
     */
    private String reportTime;

    /**
     * 受理节点
     */
    private String acceptNode;
}
