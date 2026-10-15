package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class EnergyDeviceQueryDTO extends BaseIndustryEntity {

    private String devCode;
    private String devName;
    private String businessType;
    private List<String> devTypeList;
    private List<String> stationCodeList;

}
