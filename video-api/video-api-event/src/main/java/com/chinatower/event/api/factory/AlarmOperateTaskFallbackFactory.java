package com.chinatower.event.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IAlarmOperateTaskService;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 告警研判任务
 */
@Component
public class AlarmOperateTaskFallbackFactory implements FallbackFactory<IAlarmOperateTaskService> {

    @Override
    public IAlarmOperateTaskService create(Throwable throwable) {
        return new IAlarmOperateTaskService() {
            @Override
            public Result<List<OperationTaskVO>> getTaskList(AlarmOperateTaskDTO dto) {
                return Result.fail("告警研判任务列表查询异常"+throwable.getMessage());
            }

            @Override
            public Result modifyTask(AlarmOperateTaskDTO dto) {
                return Result.fail("告警研判任务修改异常"+throwable.getMessage());
            }

        };
    }
}
