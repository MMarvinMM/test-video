package com.chinatower.emergency.dto;

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
