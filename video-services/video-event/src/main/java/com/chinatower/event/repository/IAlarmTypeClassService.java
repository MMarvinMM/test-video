package com.chinatower.event.repository;

import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.entity.Result;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.core.vo.AlarmClassParamVO;

import java.util.List;

/**
 * 告警类型业务接口
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 11:22
 */
public interface IAlarmTypeClassService {
    /**
     * 更新类型
     * @param alarmTypeClassDTO
     * @return
     */
    Result<String> updateType(AlarmTypeClassDTO alarmTypeClassDTO);

    /**
     * 获取阈值参数
     * @param alarmClassParamVO
     * @return
     */
    Result<AlarmClassParamVO> getAlarmParamInfo(AlarmClassParamVO alarmClassParamVO);

    /**
     * 保存阈值参数
     * @param alarmClassParamVO
     * @return
     */
    Result<String> saveAlarmParamInfo(AlarmClassParamVO alarmClassParamVO) throws Exception;

    /**
     * 根据告警类型编码修改告警等级
     * @param body 修改参数
     * @return 修改结果
     */
    Result<String> modifyAlarmLevel(AlarmTypeClassDTO body);
}
