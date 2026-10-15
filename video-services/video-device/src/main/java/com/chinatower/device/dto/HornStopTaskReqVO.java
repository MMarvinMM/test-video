package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 广播子平台
 * 4.27.停止广播任务请求参数实体类
 * @author huang.xiangyu
 */
@Data
public class HornStopTaskReqVO implements Serializable {


    private String taskId;

    private String acceptNode;
}
