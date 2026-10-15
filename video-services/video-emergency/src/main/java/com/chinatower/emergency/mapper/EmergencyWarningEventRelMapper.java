// EmergencyWarningEventRelMapper.java
package com.chinatower.emergency.mapper;

import com.chinatower.emergency.dto.EmergencyWarningEventRelVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmergencyWarningEventRelMapper {
    int insert(EmergencyWarningEventRelVO eventRel);
    int delete(EmergencyWarningEventRelVO eventRel);
    List<EmergencyWarningEventRelVO> selectByCondition(EmergencyWarningEventRelVO emergencyWarningEventRelVO);
}
