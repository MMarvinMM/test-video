package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmOrderImgDto implements Serializable {

    /**
     * 工单号
     */
    private String orderId;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 序号
     */
    private int serNumber;

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 属地化节点code
     */
    private String platformCode;

    /**
     * 图片视频标记位 1图片 2视频
     */
    private String type;

    /**
     * 视频url
     */
    private List<String> videoUrls;
}
