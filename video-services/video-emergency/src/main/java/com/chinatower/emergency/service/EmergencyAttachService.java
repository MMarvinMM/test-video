package com.chinatower.emergency.service;

import com.chinatower.emergency.dto.EmergencyAttachVO;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: EmergencyAttachService
 * @description: 应急相关附件的服务层接口
 * @author: fengty
 * @create: 2024-10-31 16:01
 */
public interface EmergencyAttachService {
    Map<Integer, List<EmergencyAttachVO>> getAllEmergencyAttaches(EmergencyAttachVO emergencyAttach);
    void addEmergencyAttach(Map<Integer, List<EmergencyAttachVO>> map, String businessCode, Integer businessType);
    void deleteEmergencyAttach(EmergencyAttachVO emergencyAttach);
}
