package com.chinatower.bdm.modules.layer.domain;

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
}
