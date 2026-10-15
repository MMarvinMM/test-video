package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransferIndustryDataDTO implements Serializable {

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;
}
