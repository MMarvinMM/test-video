package com.chinatower.video.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *  抓图回调接口入参实体
 */
@Data
public class SnapshotBackAssistVO implements Serializable {
    private static final long serialVersionUID = 1L;

   //通道编码
    private String channelCode;

    //图片途径
    private String imagePath;
}
