package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 光雷联动信息
 */
@Data
public class TargetFollowInfo implements Serializable {

    /**
     * 光电跟踪录像时间（单位：分钟）。不传表示不跟踪录像，默认不跟踪录像
     */
    private String cameraFollow;

    /**
     * 自动抓拍（单位：张）。不传表示不抓拍，默认不抓拍
     */
    private String autoPhoto;
}
