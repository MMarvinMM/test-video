package com.chinatower.platform.entity.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriggerRuleJson implements Serializable {

    /**
     * 区间
     */
    private List<RangeJson> rangeRule;



    /**
     * or-或  and-且
     */
    private String relation;
}
