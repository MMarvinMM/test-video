package com.chinatower.event.dto.leadertree;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class LeaderByTenantIdTreeReq extends BaseModel implements Serializable {

    /**
     * 省市区
     */
    private String cityId;
    private String countyId;
    private String provinceId;
    /**
     * 摄像机code
     */
    private String deviceCode;
    /**
     * 角色标识：8领导
     */
    private String roleFlag;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 告警来源
     */
    private String warningSource;

    /**
     * 告警类型id
     */
    private String warningTypeId;

}
