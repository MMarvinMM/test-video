package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 二次识别配置参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondExtraFieldInfoVo{
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 飞行任务id
     * */
    private String airTaskId;

    /**
     * 飞行航线id
     * */
    private String airLineId;

    /**
     * 图片Id
     * */
    private String picId;

    /**
     * 图片Id
     * */
    private String videoId;

    /**
     * 轨迹时间
     * */
    private String trailTime;
}
