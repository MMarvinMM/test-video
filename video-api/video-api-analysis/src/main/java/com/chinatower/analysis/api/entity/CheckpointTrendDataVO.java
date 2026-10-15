package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CheckpointTrendDataVO implements Serializable {

    private static final long serialVersionUID = 3255494057940176656L;
    /**
     * 数量
     * @required true
     */
    private int num;
    /**
     * 日期
     * @required true
     */
    private String label;

    private List<String> validDeviceCodes;

}
