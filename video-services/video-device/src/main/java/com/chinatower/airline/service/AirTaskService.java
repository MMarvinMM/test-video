package com.chinatower.airline.service;

import com.chinatower.airline.dto.request.*;
import com.chinatower.airline.dto.response.AirTaskRes;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirResultReq;
import com.chinatower.device.entity.dto.AirTaskReq;

import java.util.List;
import java.util.Map;

/**
 * @Author lize
 * @Date 2024/11/11 20:06
 */
public interface AirTaskService {


    /**
     * 根据id删除任务
     * @param airTaskReq
     * @return
     */
    Result deleteById(AirTaskReq airTaskReq);

    /**
     *
     * @param trigger
     * @return
     */
    InspectionPlanExpectationReq calcInspectionPlanExpectation(InspectionPlanTriggerReq trigger);

    /**
     * 查询任务列表
     * @param airTaskReq
     * @return
     */
    List<AirTaskRes> queryTaskList(AirTaskListReq airTaskReq);

    /**
     * 断点续飞
     * @param airTaskReq
     * @return
     */
    Result continueUavTask(AirTaskListReq airTaskReq);

    /**
     * 
     * @param airTaskReq
     * @return
     */
    Result checkUavTaskStatus(AirTaskListReq airTaskReq);

    /**
     *
     * @param airResultReq
     * @return
     */
    Result queryReFlyName(AirResultReq airResultReq);

    /**
     * 查询天气策略
     * @param airResultReq
     * @return
     */
    Result queryWeatherStrategy(AirResultReq airResultReq);

    /**
     * 查询任务列表(分页)
     * @param airTaskReq
     * @return
     */
    List<AirTaskRes> queryTaskListLimit(AirTaskListReq airTaskReq);

    /**
     *
     * @param airTaskReq
     * @return
     */
    Result checkUavStatus(AirTaskListReq airTaskReq);

    /**
     * 更新任务实际里程
     * @param airTaskReq
     * @return
     */
    Result updateTaskAccumulateMileage(AirTaskReq airTaskReq);

    /**
     * 批量计算任务实际里程
     * 先从 Redis 获取，获取不到再从历史轨迹中计算并缓存
     * 
     * @param taskRecordIds 任务记录ID列表
     * @return Map<taskRecordId, 实际里程>，里程单位为米
     */
    Map<String, String> batchCalculateActualMileage(List<String> taskRecordIds);

    /**
     * 终止任务
     * @param airTaskReq
     * @return
     */
    Result terminateTask(AirTaskReq airTaskReq);
}
