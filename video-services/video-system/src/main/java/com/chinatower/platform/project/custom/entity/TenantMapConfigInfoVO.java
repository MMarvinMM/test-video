package com.chinatower.platform.project.custom.entity;

import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TenantMapConfigInfoVO implements Serializable {

    private String mapType;

    private String tenantId;

    private String acceptNode;

    private String industryCode;

    private String appVerCode;

    private List<TenantMapConfigInfoSubVO> mapUrlInfoList;
}
