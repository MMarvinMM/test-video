package com.chinatower.event.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;

import java.util.List;

public interface AlarmOperateTaskService {

    Result<List<OperationTaskVO>> getTaskList(AlarmOperateTaskDTO dto);

    Result modifyTask(AlarmOperateTaskDTO dto);

}
