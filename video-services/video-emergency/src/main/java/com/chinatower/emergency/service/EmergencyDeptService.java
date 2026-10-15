package com.chinatower.emergency.service;


import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.EmergencyDeptDTO;

import java.util.List;

/**
 * @author Liqi
 */
public interface EmergencyDeptService {

    /*
    * 生成默认组别
    */
    void generateInitialDept(String eventCode);

    /*
     * 清除组织用户信息
     */
    void clearDeptUserInfo(List<String> eventCodes);

    Result addEmergencyGroup(EmergencyDeptDTO dto);

    String delEmergencyDept(EmergencyDeptDTO dto);

    List<EmergencyDeptDTO> getEmergencyGroup(EmergencyDeptDTO dto);

    Result addEmergencyDept(EmergencyDeptDTO dto);

    Object getEmergencyOrganTree(EmergencyDeptDTO dto);
}
