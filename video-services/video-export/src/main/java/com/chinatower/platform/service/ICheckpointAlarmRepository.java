package com.chinatower.platform.service;

import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.vo.CheckpointAlarmExportVO;

import java.util.List;

public interface ICheckpointAlarmRepository {



    /**
     * 查询车辆进出记录
     * @param dto
     * @return
     */
    List<CheckpointAlarmExportVO> exportCheckpointAlarmList(CheckpointAlarmDTO dto);
}
