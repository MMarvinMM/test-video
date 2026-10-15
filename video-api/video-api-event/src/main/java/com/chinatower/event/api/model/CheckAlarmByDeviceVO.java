package com.chinatower.event.api.model;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckAlarmByDeviceVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -808120948718987718L;
    private String deviceCode;
    private String deviceName;
    private int count;
    private String provinceCode;
    private String cityCode;
    private String countyCode;
    private String townCode;
    /** 告警开始时间 */
    private String alarmTimeStart;
    /** 告警结束时间 */
    private String alarmTimeEnd;
    /** 排序规则  asc desc */
    private String orderBy;
    private String longitude;
    private String latitude;
    private String vehicleNumber;
    private String time;
    private Set<String> deviceList;
}
