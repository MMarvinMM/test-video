// EmergencyWarningEventRelService.java
package com.chinatower.emergency.service;

import com.chinatower.emergency.dto.EmergencyWarningEventRelVO;
import java.util.List;

public interface EmergencyWarningEventRelService {
    int delete(EmergencyWarningEventRelVO emergencyWarningEventRelVO);
    List<EmergencyWarningEventRelVO> selectByCondition(EmergencyWarningEventRelVO emergencyWarningEventRelVO);

    void connectedEvent(EmergencyWarningEventRelVO emergencyWarningEventRelVO);

    boolean checkConnectedEvent(EmergencyWarningEventRelVO emergencyWarningEventRelVO);
}
