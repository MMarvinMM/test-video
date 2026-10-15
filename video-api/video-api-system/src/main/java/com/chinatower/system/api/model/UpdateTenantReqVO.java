package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 批量更新请求vo
 */
@Data
@ToString
public class UpdateTenantReqVO {
    private String isOperation;
    private String operationModifier;
    private String alarmLocation;
    private List<TenantInfoVO> customerInfoList;

    private String operationType;

    private String operationNode;

    private String currentLocationCode;

    private Boolean syncFlag = false;
}
