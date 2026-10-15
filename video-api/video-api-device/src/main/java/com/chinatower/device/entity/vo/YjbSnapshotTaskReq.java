package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应急部摄像机抓图任务请求参数。
 */
@Data
public class YjbSnapshotTaskReq implements Serializable {

    /** 创建任务时传入的设备通道组合列表，最多50条。 */
    private List<YjbSnapshotDeviceChannelReq> deviceChannels;

    /** 查询任务时传入的服务端任务编码。 */
    private String taskId;
}
