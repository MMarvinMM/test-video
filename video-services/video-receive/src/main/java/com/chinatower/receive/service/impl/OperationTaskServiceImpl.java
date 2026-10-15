package com.chinatower.receive.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.IAlarmOperateTaskService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import com.chinatower.receive.service.IOperationTaskService;
import com.chinatower.receive.vo.dto.OperationTaskListRequest;
import com.chinatower.receive.vo.dto.OperationTaskListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作任务服务类
 */
@Slf4j
@Service
public class OperationTaskServiceImpl implements IOperationTaskService {
    
    @Autowired
    private IAlarmOperateTaskService iAlarmOperateTaskService;
    
    /**
     * 查询操作任务列表
     * 
     * @param request 查询请求参数
     * @return 任务列表响应数据
     */
    @Override
    public List<OperationTaskListResponse> getOperationTaskList(OperationTaskListRequest request) {
        log.info("调用操作任务列表接口, 请求参数: {}", JSONObject.toJSONString(request));
        AlarmOperateTaskDTO alarmOperateTaskDTO = new AlarmOperateTaskDTO();
        BeanUtils.copyProperties(request,alarmOperateTaskDTO);
        Result<List<OperationTaskVO>> taskResult = iAlarmOperateTaskService.getTaskList(alarmOperateTaskDTO);
        log.info("调用操作任务列表接口, 返回参数: {}", JSONObject.toJSONString(taskResult));
        List<OperationTaskListResponse> taskVOList = new ArrayList<>();
        if(taskResult!=null && taskResult.getCode() == ResultCode.SUCCESS.getCode()){
            if(!CollectionUtils.isEmpty(taskResult.getData())){
                for(OperationTaskVO operationTaskVO:taskResult.getData()){
                    OperationTaskListResponse operationTaskListResponse = new OperationTaskListResponse();
                    BeanUtils.copyProperties(operationTaskVO,operationTaskListResponse);
                    taskVOList.add(operationTaskListResponse);
                }
            }
        }
        return taskVOList;
    }

    @Override
    public OperationTaskListResponse getUserTaskInfo(String userId) {
        OperationTaskListRequest request = new OperationTaskListRequest();
        request.setUser(userId);
        request.setSingleLabel("999999");
        return getOperationTaskList(request).get(Constants.ZERO_NUM);
    }


}
