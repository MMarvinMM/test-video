package com.chinatower.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeUnit implements Serializable {

    private RangeValueJson min;

    private RangeValueJson max;

    /**
     * 字段 人/万人/户/万户
     */
    private String unit;
}
