package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalInOutBalanceRes {
    /**
     * 耕地的进出平衡计算结果的GUID
     * */
    private String guid;
}
