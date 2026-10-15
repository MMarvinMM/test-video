package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApiLeaderByTenantIdTreeReq extends BaseModel implements Serializable {

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 摄像机编码
     * @required false
     */
    private String deviceCode;

    /**
     * 角色标识：8领导
     * @required false
     */
    private String roleFlag;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;

    /**
     * 告警类型id
     */
    private String warningTypeId;

}
