package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnergyDeviceStatusStatisticsQueryReq extends BaseIndustryEntity {

    private List<String> stationCodeList;
    private String stationName;
    private String stationCode;

    private String businessType;
}
