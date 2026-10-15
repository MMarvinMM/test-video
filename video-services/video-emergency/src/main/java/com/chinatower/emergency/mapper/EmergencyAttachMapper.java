package com.chinatower.emergency.mapper;

import com.chinatower.emergency.dto.EmergencyAttachVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: EmergencyAttachMapper
 * @description: 应急相关附件mapper
 * @author: fengty
 * @create: 2024-10-31 16:03
 */
@Mapper
public interface EmergencyAttachMapper {

    List<EmergencyAttachVO> selectAllEmergencyAttaches(EmergencyAttachVO emergencyAttach);

    void insertEmergencyAttach(List<EmergencyAttachVO> emergencyAttachList);


    void deleteEmergencyAttach(EmergencyAttachVO emergencyAttach);
}
