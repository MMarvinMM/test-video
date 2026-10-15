package com.chinatower.platform.entity.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeJson implements Serializable {

    private RangeValueJson min;

    private RangeValueJson max;

    /**
     * 字段
     */
    private String field;
}
