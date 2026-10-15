package com.chinatower.emergency.service;





import com.chinatower.emergency.dto.*;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.CommonFeignEntity;

import java.util.List;


/**
 * @author Liqi
 */
public interface EmergencyUserService {

    Result addCommander( EmergencyUserDTO dto);

    String updateCommander(EmergencyUserDTO dto);

    List<EmergencyUserVO> getEmergencyUser(EmergencyUserDTO dto);

    String delEmergencyUser(EmergencyUserDTO dto);

    EmergencyUserVO checkUser();

    Result saveLngLatByUser(SaveLngLatParam dto,Integer flag);

    Result getUserTrail(QueryTrailsParam queryTrailsParam);

    Result<List<EmergencyUserLngLatDTO>> getUserPosition(SaveLngLatParam dto);

    List<EmergencyUserInfoDTO> getAllUserByEventCode(EmergencyEventInfoVO emergencyEventInfo);
}
