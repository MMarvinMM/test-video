package com.chinatower.bdm.modules.layer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeValueJson implements Serializable {

    private String value;

    /**
     * lt 小于  lte 小于等于
     */
    private String symbol;
}
