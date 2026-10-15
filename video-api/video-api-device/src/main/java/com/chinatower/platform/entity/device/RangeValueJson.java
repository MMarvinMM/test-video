package com.chinatower.platform.entity.device;

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
     * <  <=
     */
    private String symbol;
}
