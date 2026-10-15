package com.chinatower.bdm.api.factory;


import com.chinatower.bdm.api.InspectionAPI;
import com.chinatower.bdm.api.entity.InspectionTaskListVO;
import com.chinatower.bdm.api.entity.InspectionTaskNoPageParam;
import com.chinatower.bdm.api.entity.InspectionTaskParamVO;
import com.chinatower.bdm.api.entity.InspectionTaskVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InspectionFallbackFactory implements FallbackFactory<InspectionAPI> {

    @Override
    public InspectionAPI create(Throwable throwable) {
        return new InspectionAPI() {
            @Override
            public Result<List<InspectionTaskListVO>> getTaskListByCondition(InspectionTaskNoPageParam inspectionTaskNoPageParam) {
                return Result.fail("行业大屏获取巡查任务列表调用失败"+throwable.getMessage());
            }

            @Override
            public Result<List<InspectionTaskListVO>> getToDoTaskList(InspectionTaskNoPageParam inspectionTaskNoPageParam) {
                return Result.fail("我的待办调用失败"+throwable.getMessage());
            }

            @Override
            public Result<InspectionTaskVO> getTaskInfo(String taskId) {
                return Result.fail("巡查任务详情调用失败"+throwable.getMessage());
            }

            @Override
            public Result<InspectionTaskVO> claimTask(InspectionTaskParamVO inspectionTaskParamVO) {
                return Result.fail("处理巡查任务调用失败"+throwable.getMessage());
            }

            @Override
            public Result getTaskTrail(InspectionTaskNoPageParam inspectionTaskNoPageParam) {
                return Result.fail("查询巡查轨迹调用失败"+throwable.getMessage());
            }

            @Override
            public Result<InspectionTaskVO> getTaskInfoByCondition(InspectionTaskNoPageParam inspectionTaskNoPageParam) {
                return Result.fail("根据网格id查询巡查任务详情(取巡查日期最新的一条)调用失败"+throwable.getMessage());
            }


        };
    }
}
