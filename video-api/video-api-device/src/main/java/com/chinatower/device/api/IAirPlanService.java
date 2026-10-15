package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AirPlanFallbackFactory;
import com.chinatower.device.entity.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 计划管理
 */
@FeignClient(contextId = "airPlanService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirPlanFallbackFactory.class)
public interface IAirPlanService {

    /**
     * 计划管理列表查询
     * @param vo
     * @return
     */
    @PostMapping("/airPlan/findByCondition")
    Result findByCondition(@RequestBody AirPlanPageReq vo);

    /**
     * 根据id查询详情
     * @param planId
     * @return
     */
    @GetMapping("/airPlan/findAirPlanById")
    Result findAirPlanById(@RequestParam("planId") Integer planId);

    /**
     * 新增计划管理
     * @param vo
     * @return
     */
    @PostMapping("/airPlan/addAirPlan")
    Result addAirPlan(@Validated @RequestBody AirPlanAddOrEditReq vo);

    /**
     * 编辑计划管理
     * @param vo
     * @return
     */
    @PostMapping("/airPlan/editAirPlan")
    Result editAirPlan(@Validated @RequestBody AirPlanAddOrEditReq vo);

    /**
     * 删除计划管理
     * @param vo
     * @return
     */
    @PostMapping("/airPlan/deleteByPlanId")
    Result deleteByPlanId(@RequestBody AirPlanAddOrEditReq vo);

    /**
     * 根据计划新增的数量自动生成一个名称“新建计划N”
     * @param tenantId
     * @param industryCode
     * @return
     */
    @GetMapping("/airPlan/getPlanName")
    Result getPlanName(@RequestParam("tenantId")String tenantId,@RequestParam("industryCode")String industryCode);

    /**
     * 唯一性校验，重复时输入框高亮显示，同时在输入框下方显示提示“名称已存在！请重新输入。
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param planName 计划名称
     * @param id id
     * @return
     */
    @GetMapping("/airPlan/planNameIsRepeat")
    Result planNameIsRepeat(@RequestParam(value = "tenantId",required = false)String tenantId,
                            @RequestParam(value = "industryCode",required = false)String industryCode,
                            @RequestParam(value = "planName",required = false)String planName,
                            @RequestParam(value = "id",required = false)Integer id);

    /**
     * 计划名称自动填充为“复制的计划名称_复制N”
     * @param planId
     * @param tenantId
     * @param industryCode
     * @return
     */
    @GetMapping("/airPlan/copyPlanName")
    Result copyPlanName(@RequestParam("planId") String planId,
                        @RequestParam("tenantId")String tenantId,
                        @RequestParam("industryCode")String industryCode);

    /**
     * 启用按钮（巡检计划明细启用或关闭）
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airPlan/updateIsItEnabled")
    Result updateIsItEnabled(@RequestBody AirTaskEnableReq airTaskReq);

    /**
     * HMS 飞行告警短信：根据无人机编码查询当前飞行计划的安全员手机号（去重）
     */
    @GetMapping("/airPlan/querySafetyOfficerPhonesByFlyingUav")
    Result<List<String>> querySafetyOfficerPhonesByFlyingUav(@RequestParam("uavCode") String uavCode);

    /**
     * HMS 飞行告警短信：根据无人机编码查询无人机名称
     */
    @GetMapping("/airPlan/queryUavNameByUavCode")
    Result<String> queryUavNameByUavCode(@RequestParam("uavCode") String uavCode);

    /**
     * 根据id删除计划下任务
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/deleteById")
    Result deleteById(@RequestBody AirTaskReq airTaskReq);

    /**
     * 查询任务列表
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/queryTaskList")
    TableDataInfo queryTaskList(@RequestBody AirTaskListReq airTaskReq);

    /**
     * 断点续飞
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/continueUavTask")
    Result continueUavTask(@RequestBody AirTaskListReq airTaskReq);

    /**
     * 校验无人机状态
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/checkUavStatus")
    Result checkUavStatus(@RequestBody AirTaskListReq airTaskReq);

    /**
     * 检查无人机状态
     * @param airTaskReq
     * @return
     */
    @PostMapping("/airTask/checkUavTaskStatus")
    Result checkUavTaskStatus(@RequestBody AirTaskListReq airTaskReq);

    /**
     * 一键复飞名称查询
     */
    @PostMapping("/airTask/queryReFlyName")
    Result queryReFlyName(@RequestBody AirResultReq airResultReq);

    /**
     * 任务天气策略查询
     */
    @PostMapping("/airTask/queryWeatherStrategy")
    Result queryWeatherStrategy(@RequestBody AirResultReq airResultReq);
}
