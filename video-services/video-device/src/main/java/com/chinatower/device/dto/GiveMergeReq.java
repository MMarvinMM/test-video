package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.device.DeviceChannelVO;
import lombok.Data;

import java.util.List;

@Data
public class GiveMergeReq extends BaseDataScopeEntity {
    private List<DeviceChannelVO> deviceChannelList;
    private List<String> alarmTypeList;
}
