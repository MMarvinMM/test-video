package com.chinatower.emergency.mapper;

import com.chinatower.emergency.dto.EmergencyEventInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: EmergencyEventInfoMapper
 * @description: 事件mapper
 * @author: fengty
 * @create: 2024-10-29 10:32
 */
@Mapper
public interface EmergencyEventInfoMapper {
    int insert(EmergencyEventInfoVO eventInfo);
    int update(EmergencyEventInfoVO eventInfo);
    EmergencyEventInfoVO selectByEventCode(EmergencyEventInfoVO eventInfo);

    List<EmergencyEventInfoVO> selectList(EmergencyEventInfoVO eventInfo);

    void delete(EmergencyEventInfoVO eventInfo);
}
