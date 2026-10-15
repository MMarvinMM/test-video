package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.entity.dto.AirResultReq;
import com.chinatower.airline.dto.request.AirTaskListReq;
import com.chinatower.device.entity.dto.AirTaskReq;
import com.chinatower.airline.service.AirTaskService;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

/**
 * @Author lize
 * @Date 2024/11/11 20:07
 */
@Slf4j
@RestController
@RequestMapping("/airTask")
public class AirTaskController {

    @Autowired
    private AirTaskService airTaskService;

    /**
     * 根据id删除计划下任务
     * @param airTaskReq
     * @return
     */
    @PostMapping("/deleteById")
    public Result deleteById(@RequestBody AirTaskReq airTaskReq) {
        log.info("AirTaskController-deleteById-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.deleteById(airTaskReq);
    }

    /**
     * 查询任务列表
     * @param airTaskReq
     * @return
     */
    @PostMapping("/queryTaskList")
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code",deviceColumn = "b.uav_code")
    public TableDataInfo queryTaskList(@RequestBody AirTaskListReq airTaskReq) {
        log.info("AirTaskController-queryTaskList-req:{}", JSON.toJSONString(airTaskReq));
        return getDataTable(airTaskService.queryTaskList(airTaskReq));
    }

    /**
     * 断点续飞
     * @param airTaskReq
     * @return
     */
    @PostMapping("/continueUavTask")
    public Result continueUavTask(@RequestBody AirTaskListReq airTaskReq) {
        log.info("AirTaskController-continueUavTask-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.continueUavTask(airTaskReq);
    }

    /**
     * 校验无人机状态
     * @param airTaskReq
     * @return
     */
    @PostMapping("/checkUavStatus")
    public Result checkUavStatus(@RequestBody AirTaskListReq airTaskReq) {
        log.info("AirTaskController-checkUavStatus-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.checkUavStatus(airTaskReq);
    }

    /**
     * 检查无人机状态
     * @param airTaskReq
     * @return
     */
    @PostMapping("/checkUavTaskStatus")
    public Result checkUavTaskStatus(@RequestBody AirTaskListReq airTaskReq) {
        log.info("AirTaskController-checkUavTaskStatus-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.checkUavTaskStatus(airTaskReq);
    }

    /**
     * 一键复飞名称查询
     */
    @PostMapping("/queryReFlyName")
    public Result queryReFlyName(@RequestBody AirResultReq airResultReq) {
        log.info("AirTaskController-queryReFlyName-req:{}", JSON.toJSONString(airResultReq));
        return airTaskService.queryReFlyName(airResultReq);
    }

    /**
     * 任务天气策略查询
     */
    @PostMapping("/queryWeatherStrategy")
    public Result queryWeatherStrategy(@RequestBody AirResultReq airResultReq) {
        log.info("AirTaskController-queryWeatherStrategy-req:{}", JSON.toJSONString(airResultReq));
        return airTaskService.queryWeatherStrategy(airResultReq);
    }

    /**
     * 查询任务列表
     * @param airTaskReq
     * @return
     */
    @PostMapping("/queryTaskListLimit")
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code",deviceColumn = "l.uav_code")
    public TableDataInfo queryTaskListLimit(@RequestBody AirTaskListReq airTaskReq) {
        log.info("AirTaskController-queryTaskListLimit-req:{}", JSON.toJSONString(airTaskReq));
        return getDataTable(airTaskService.queryTaskListLimit(airTaskReq));
    }

    /**
     * 更新任务实际里程(ws使用)
     * @param airTaskReq
     * @return
     */
    @PostMapping("/updateTaskAccumulateMileage")
    public Result updateTaskAccumulateMileage(@RequestBody AirTaskReq airTaskReq) {
        log.info("AirTaskController-updateTaskAccumulateMileage-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.updateTaskAccumulateMileage(airTaskReq);
    }

    /**
     * 批量计算任务实际里程
     * 先从 Redis 获取，获取不到再从历史轨迹中计算并缓存
     * 如果任务数量大于10，会分成3组线程并行计算
     * 
     * @param taskRecordIds 任务记录ID列表
     * @return Map<taskRecordId, 实际里程>，里程单位为米
     */
    @PostMapping("/batchCalculateActualMileage")
    public Result batchCalculateActualMileage(@RequestBody List<String> taskRecordIds) {
        log.info("AirTaskController-batchCalculateActualMileage-req:{}", JSON.toJSONString(taskRecordIds));
        try {
            Map<String, String> mileageMap = airTaskService.batchCalculateActualMileage(taskRecordIds);
            return Result.data(mileageMap);
        } catch (Exception e) {
            log.error("批量计算任务实际里程异常", e);
            return Result.fail("批量计算任务实际里程失败: " + e.getMessage());
        }
    }

    /**
     * 终止任务
     * @param airTaskReq
     * @return
     */
    @PostMapping("/terminateTask")
    public Result terminateTask(@RequestBody AirTaskReq airTaskReq) {
        log.info("AirTaskController-terminateTask-req:{}", JSON.toJSONString(airTaskReq));
        return airTaskService.terminateTask(airTaskReq);
    }

}
