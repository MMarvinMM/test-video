package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 应急部抓图已完成明细。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YjbSnapshotItemRsp implements Serializable {

    /** 摄像机编码。 */
    private String deviceCode;

    /** 通道编码。 */
    private String channelCode;

    /** 加签后的图片地址。 */
    private String imgUrl;
}
