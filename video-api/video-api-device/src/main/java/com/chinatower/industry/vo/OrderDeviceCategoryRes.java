package com.chinatower.industry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderDeviceCategoryRes {

    /**
     * 摄像机类型编码
     */
    private String categoryCode;

    /**
     * 摄像机类型编码
     */
    private String categoryName;

    /**
     * 摄像机数量
     */
    private long deviceCount;


}
