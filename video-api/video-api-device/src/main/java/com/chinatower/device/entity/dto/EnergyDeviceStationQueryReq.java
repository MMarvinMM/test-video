package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnergyDeviceStationQueryReq extends BaseIndustryEntity {
    private Integer pageNum;
    private Integer pageSize;
    private String businessType;
    private String stationCode;
    private String stationName;
    private String provinceId;
    private String cityId;
    private String countyId;
    private List<String> stationCodeList;

    private List<String> orderStationCodeList;
}
