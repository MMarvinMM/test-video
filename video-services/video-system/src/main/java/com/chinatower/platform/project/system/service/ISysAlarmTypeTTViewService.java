package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.domain.SysAlarmType;
import com.chinatower.platform.project.system.domain.vo.SysAlarmTypeVO;

import java.util.List;

public interface ISysAlarmTypeTTViewService {

    List<SysAlarmType> getAlarmsTypeList(SysAlarmTypeVO sysAlarmTypeVO);

    Result addAlarmsTypeList(List<SysAlarmTypeVO> sysAlarmTypeVOList);

    Result updateAlarmsType(SysAlarmTypeVO sysAlarmTypeVO);

    void removeAlarmType(SysAlarmTypeVO sysAlarmTypeVO);

    Result checkAlarmsName(SysAlarmTypeVO sysAlarmTypeVO);
}
