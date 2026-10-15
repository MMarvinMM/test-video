package com.chinatower.emergency.mapper;


import com.chinatower.emergency.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Liqi
 */
@Repository
public interface EmergencyDeptMapper {

    void generatInitialDept(EmergencyDeptDTO emergencyDeptDTO);

    void addEmergencyGroup(EmergencyDeptDTO dto);

    List<EmergencyDeptDTO> getEmergencyGroup(EmergencyDeptDTO dto);

    List<EmergencyGroupVO> getEmergencyDeptList(EmergencyDeptDTO dto);

    List<EmergencyUserDTO> getEmergencyUserList(Set<String> deptIdSet);

    void delEmergencyGroup(EmergencyDeptDTO dto);

    EmergencyDeptDTO getEmergencyDeptById(String id);

    List<String> getIdList(List<String> eventCodes);

    void clearDeptInfo(List<String> idList);

    void clearUserInfo(List<String> idList);

    void updateEventCodeByDeptIds(@Param("deptIds") List<Integer> deptIds, @Param("digitalId") String digitalId, @Param("modifier") String modifier);

    void batchSave(List<DigitalDeptUserTreeVO> list);

    void deleteByEventCode(String digitalId);

    List<DigitalDeptUserTreeVO> getDeptByEventCode(String digitalId);

    String checkImportDept(String eventCode);

    String findCommanderDept(String digitalId);
}