package com.chinatower.event.repository;

import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.dto.CheckpointWarningOrderAddDTO;

import java.util.List;

public interface ICheckpointAlarmRepository {

    /**
     * 查询车辆进出记录
     * @param dto
     * @return
     */
    List<CheckpointAlarmDTO> getCheckpointAlarmList(CheckpointAlarmDTO dto);

    List<CheckAlarmByDeviceVO> getCheckAlarmByDevice(CheckAlarmByDeviceVO dto);

    List<CheckAlarmByDeviceVO> getCheckpointAlarmInfo(CheckpointAlarmDTO dto);

    /**
     * 批量添加车辆进出记录
     * @param records 批量添加记录列表
     * @return 添加结果说明
     */
    String batchAddWarningOrder(List<CheckpointWarningOrderAddDTO> records);

}
