package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import com.chinatower.event.service.AlarmOperateTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * AI告警研判任务和研判结果查询
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@RestController
@RequestMapping("/alarm/operateTask")
public class AlarmOperateTaskController {

    @Autowired
    private AlarmOperateTaskService alarmOperateTaskService;

    /**
     * 告警研判任务列表查询
     * @param dto
     * @return
     */
    @PostMapping("/getTaskList")
    public Result<List<OperationTaskVO>> getTaskList(@RequestBody AlarmOperateTaskDTO dto) {
        return alarmOperateTaskService.getTaskList(dto);
    }

    /**
     * 告警研判任务修改
     * @param dto
     * @return
     */
    @PostMapping("/modifyTask")
    public Result modifyTask(@RequestBody AlarmOperateTaskDTO dto) {
        return alarmOperateTaskService.modifyTask(dto);
    }


}
