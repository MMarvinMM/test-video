package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

@Data
public class AlarmTypeReq extends BaseDataScopeEntity {

    private List<String> enumCodeList;

    private String alarmTypeName;
}
