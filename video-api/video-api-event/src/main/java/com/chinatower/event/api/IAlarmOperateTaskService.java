package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.AlarmOperateTaskFallbackFactory;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "alarmOperateTaskService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = AlarmOperateTaskFallbackFactory.class)
public interface IAlarmOperateTaskService {

    /**
     * 告警研判任务列表查询
     * @param dto
     * @return
     */
    @PostMapping("/alarm/operateTask/getTaskList")
    Result<List<OperationTaskVO>> getTaskList(@RequestBody AlarmOperateTaskDTO dto) ;

    /**
     * 告警研判任务修改
     * @param dto
     * @return
     */
    @PostMapping("/alarm/operateTask/modifyTask")
    Result modifyTask(@RequestBody AlarmOperateTaskDTO dto);


}
