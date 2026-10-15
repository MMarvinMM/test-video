package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * AIS船舶画像返回实体类
 */
@Data
public class RadarCommonBehavior implements Serializable {

    /**
     * 行为名称
     */
    private String behaviorName;

    /**
     * 次数
     */
    private Integer number;

}
