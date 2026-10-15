package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.util.List;

@Data
public class DeviceLabelReq extends BaseIndustryEntity {

    private List<String> deviceCodeList;
}
