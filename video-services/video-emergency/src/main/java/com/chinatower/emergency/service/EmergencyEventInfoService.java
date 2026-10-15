package com.chinatower.emergency.service;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.emergency.dto.EmergencyEventInfoVO;
import com.chinatower.emergency.dto.EmergencyWarningEventRelVO;

import java.util.List;

public interface EmergencyEventInfoService {
    void addEmergencyEventInfoVO(EmergencyEventInfoVO eventInfo) throws Exception;
    void updateEmergencyEventInfoVO(EmergencyEventInfoVO eventInfo) throws Exception;
    EmergencyEventInfoVO getEmergencyEventInfoVO(String eventCode);

    List<EmergencyEventInfoVO> getEmergencyEventInfoList(EmergencyEventInfoVO eventInfo);

    void deleteEvent(EmergencyEventInfoVO eventInfo);

    List<EmergencyWarningEventRelVO> getEventWarningList(String eventCode);

    Object getLayerForEventInfo(EmergencyEventInfoVO eventInfo);
}