package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysAlarmType;
import com.chinatower.platform.project.system.domain.vo.SysAlarmTypeVO;

import java.util.List;

public interface SysAlarmTypeTTViewMapper {

    List<SysAlarmType> getAlarmsTypeList(SysAlarmTypeVO sysAlarmTypeVO);

    void addAlarmsTypeList(List<SysAlarmTypeVO> sysAlarmTypeVOList);

    Integer updateAlarmsType(SysAlarmTypeVO sysAlarmTypeVO);

    void removeAlarmType(SysAlarmTypeVO sysAlarmTypeVO);

    Integer checkAlarmsName(SysAlarmTypeVO sysAlarmTypeVO);
}
