package com.chinatower.emergency.mapper;


import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.*;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Liqi
 */
@Repository
public interface EmergencyUserMapper {

    void addCommander(EmergencyUserDTO dto);

    void delEmergencyUserByDept(EmergencyUserDTO dto);

    List<EmergencyUserVO> getEmergencyUser(EmergencyUserDTO dto);

    EmergencyUserVO checkUser(String userName);

    void updateEmergencyUserLngLat(EmergencyUserLngLatDTO emergencyUserLngLatDTO);

    int checkUserLngLat(EmergencyUserLngLatDTO emergencyUserLngLatDTO);

    void addEmergencyUserLngLat(EmergencyUserLngLatDTO emergencyUserLngLatDTO);

    List<EmergencyUserLngLatDTO> getUserPosition(CommonFeignEntity commonFeignEntity);

    void updateCommander(EmergencyUserDTO dto);

    void deleteByEventCode(String digitalId);

    List<EmergencyUserDTO> getUserByDeptId(List<String> keysList);

    void batchSave(List<EmergencyUserDTO> emergencyUserList);

    Integer checkImportUser(String eventCode);

    List<EmergencyUserInfoDTO> getAllUserByEventCode(String eventCode);
}