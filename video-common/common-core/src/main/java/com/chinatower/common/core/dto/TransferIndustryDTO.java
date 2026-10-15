package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransferIndustryDTO implements Serializable {

    /**
     * 客户id
     */
    private String tenantId;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 当前行业信息
     */
    private TransferIndustryDataDTO appInfo;

    /**
     * 变更行业信息
     */
    private TransferIndustryDataDTO targetApp;
}
