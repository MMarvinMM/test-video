package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceUserHistoryVO extends BaseDataScopeEntity {

    private String deviceCode;

    private String channelCode;


}
