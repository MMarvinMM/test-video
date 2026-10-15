package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OperationPushRelVO implements Serializable {

    private String acceptNode;

    private String acceptNodeName;

    private String pushStatus;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private List<String> parentLocationCodeList;

    private Integer pageSize;

    private Integer pageNum;

    private String pushAcceptNode;

    private String locationCode;
}
