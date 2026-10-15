package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 *  下载工单单张图片或者视频参数
 *
 * @date 2024年7月8日
 * @author lisy
 */
@Data
public class DownloadSingleImgOrVideoReq {

    /**
     * 工单ID
     */
    private String warningOrderId;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 图片或者视频序号
     */
    private Integer serNumber;
}
