package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/** 应急部抓图任务下发失败明细。 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YjbSnapshotFailedItemRsp implements Serializable {

    /** 摄像机编码。 */
    private String deviceCode;

    /** 通道编码。 */
    private String channelCode;

    /** 下发失败原因。 */
    private String errorMessage;
}
