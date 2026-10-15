package com.chinatower.receive.service;

import com.chinatower.receive.vo.dto.OperationTaskListRequest;
import com.chinatower.receive.vo.dto.OperationTaskListResponse;

import java.util.List;

public interface IOperationTaskService {

    /**
     * 研判任务列表查询
     * @param request
     * @return
     */
    List<OperationTaskListResponse> getOperationTaskList(OperationTaskListRequest request);

    /**
     * 查询用户任务是否开启
     * @param userId
     * @return
     */
    OperationTaskListResponse getUserTaskInfo(String userId);

}
