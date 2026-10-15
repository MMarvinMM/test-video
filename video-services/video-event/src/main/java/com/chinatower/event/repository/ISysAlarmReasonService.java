package com.chinatower.event.repository;


import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.dto.AlarmReasonSaveDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;

import java.util.List;

public interface ISysAlarmReasonService {

    /**
     * 查询告警类型原因列表
     * @param param 查询条件
     * @return 告警类型原因信息
     */
    List<AlarmReasonVO> list(AlarmReasonQueryDTO param);

    /**
     * 根据关键字查询告警类型原因列表
     * @param param 查询条件
     * @return 告警类型原因信息
     */
    List<AlarmReasonVO> queryAlarmReasonList(AlarmReasonQueryDTO param);

    /**
     * 保存告警类型原因信息
     * @param param 告警类型原因信息
     * @return 影响结果
     */
    void save(AlarmReasonSaveDTO param);

}
