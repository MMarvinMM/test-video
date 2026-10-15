package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * AIS船舶画像返回实体类
 */
@Data
public class RadarAisInfoChangeBehavior implements Serializable {

    /**
     * 变更时间
     */
    private String changeTime;

    /**
     * 变更内容
     */
    private String changeContent;

    /**
     * 变更前
     */
    private String changeBefore;

    /**
     * 变更后
     */
    private String changeAfter;
}
