package com.chinatower.device.dto;

import lombok.Data;

@Data
public class FunctionAlgorthmVO {
    /**
     * 功能小类
     */
    private String functionCode;
    /**
     * 图标名称
     */
    private String iconCode;
    /**
     * 图标颜色
     */
    private String iconColour;

    /**
     * 算法唯一编码
     */
    private String algorithmCode;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 算法描述
     */
    private String algorithmDescription;
}
