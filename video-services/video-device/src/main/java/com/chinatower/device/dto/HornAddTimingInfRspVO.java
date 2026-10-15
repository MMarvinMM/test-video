package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 广播子平台
 * 4.24.创建广播任务返回实体类
 * @author huang.xiangyu
 */
@Data
public class HornAddTimingInfRspVO implements Serializable {

    //调用创建任务接口返回的任务id
    private String taskId;

}
