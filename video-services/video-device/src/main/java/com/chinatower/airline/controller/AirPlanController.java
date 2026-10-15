package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.service.AirPlanService;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirPlanAddOrEditReq;
import com.chinatower.device.entity.dto.AirPlanPageReq;
import com.chinatower.device.entity.dto.AirTaskEnableReq;
import com.chinatower.platform.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author lize
 * @Date 2024/11/4 11:20
 */
@Slf4j
@RestController
@RequestMapping("/airPlan")
public class AirPlanController {

    @Autowired
    private AirPlanService airPlanService;

    /**
     * 功能描述：计划管理列表查询
     *
     * @param vo
     * @return
     */
    @PostMapping("/findByCondition")
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public Result findByCondition(@RequestBody AirPlanPageReq vo) {
        log.info("AirPlanController-findByCondition-req:{}", JSON.toJSONString(vo));
        return Result.data(airPlanService.findByCondition(vo));
    }

    /**
     * 根据id查询详情
     *
     * @param planId
     * @return
     */
    @GetMapping("/findAirPlanById")
    public Result findAirPlanById(@RequestParam("planId") Integer planId) {
        log.info("AirPlanController-findAirPlanById-req:{}", JSON.toJSONString(planId));
        return Result.data(airPlanService.findAirPlanById(planId));
    }

    /**
     * 功能描述：新增计划管理
     *
     * @param vo
     * @return
     */
    @PostMapping("/addAirPlan")
    public Result addAirPlan(@Validated @RequestBody AirPlanAddOrEditReq vo) {
        log.info("AirPlanController-addAirPlan-req:{}", JSON.toJSONString(vo));
        return Result.data(airPlanService.addOrEditAirPlan(vo, Constants.ZERO));
    }

    /**
     * 功能描述：编辑计划管理
     *
     * @param vo
     * @return
     */
    @PostMapping("/editAirPlan")
    public Result editAirPlan(@Validated @RequestBody AirPlanAddOrEditReq vo) {
        log.info("AirPlanController-editAirPlan-req:{}", JSON.toJSONString(vo));
        return Result.data(airPlanService.addOrEditAirPlan(vo, Constants.ZERO));
    }

    /**
     * 功能描述：删除计划管理
     *
     * @param vo
     * @return
     */
    @PostMapping("/deleteByPlanId")
    public Result deleteByPlanId(@RequestBody AirPlanAddOrEditReq vo) {
        log.info("AirPlanController-deleteByPlanId-req:{}", JSON.toJSONString(vo));
        return Result.data(airPlanService.deleteByPlanId(vo));
    }

    /**
     * 系统根据计划新增的数量自动生成一个名称“新建计划N”，可修改。
     *
     * @param tenantId
     * @param industryCode
     * @return
     */
    @GetMapping("/getPlanName")
    public Result getPlanName(@RequestParam("tenantId") String tenantId, @RequestParam("industryCode") String industryCode) {
        return Result.data(airPlanService.getPlanName(tenantId, industryCode));
    }

    /**
     * 唯一性校验，重复时输入框高亮显示，同时在输入框下方显示提示“名称已存在！请重新输入。
     *
     * @param tenantId     租户id
     * @param industryCode 行业编码
     * @param planName     计划名称
     * @param id           id
     * @return
     */
    @GetMapping("/planNameIsRepeat")
    public Result planNameIsRepeat(@RequestParam(value = "tenantId", required = false) String tenantId,
                                   @RequestParam(value = "industryCode", required = false) String industryCode,
                                   @RequestParam(value = "planName", required = false) String planName,
                                   @RequestParam(value = "id", required = false) Integer id) {
        return Result.data(airPlanService.planNameIsRepeat(tenantId, industryCode, planName, id));
    }

    /**
     * 计划名称自动填充为“复制的计划名称_复制N”
     *
     * @param planId
     * @param tenantId
     * @param industryCode
     * @return
     */
    @GetMapping("/copyPlanName")
    public Result copyPlanName(@RequestParam("planId") String planId,
                               @RequestParam("tenantId") String tenantId,
                               @RequestParam("industryCode") String industryCode
    ) {
        return Result.data(airPlanService.copyPlanName(planId, tenantId, industryCode));
    }

    /**
     * 启用按钮
     *
     * @param airTaskReq
     * @return
     */
    @PostMapping("/updateIsItEnabled")
    public Result updateIsItEnabled(@RequestBody AirTaskEnableReq airTaskReq) {
        log.info("AirPlanController-updateIsItEnabled-req:{}", JSON.toJSONString(airTaskReq));
        return airPlanService.updateIsItEnabled(airTaskReq);
    }

    /**
     * 查询当前登录人历史填写过的安全员信息（最新10条），支持按安全员姓名模糊搜索
     * <p>
     * 下拉展示：安全员 + 联系电话（不脱敏）
     */
    @GetMapping("/querySafetyOfficerMemory")
    public Result querySafetyOfficerMemory(@RequestParam(value = "safetyOfficer", required = false) String safetyOfficer) {
        return airPlanService.querySafetyOfficerMemory(safetyOfficer);
    }

    /**
     * HMS 飞行告警短信：根据无人机编码查询当前飞行计划的安全员手机号（去重）
     */
    @GetMapping("/querySafetyOfficerPhonesByFlyingUav")
    public Result<List<String>> querySafetyOfficerPhonesByFlyingUav(@RequestParam("uavCode") String uavCode) {
        return airPlanService.selectSafetyOfficerPhonesByFlyingUav(uavCode);
    }

    /**
     * HMS 飞行告警短信：根据无人机编码查询无人机名称
     */
    @GetMapping("/queryUavNameByUavCode")
    public Result<String> queryUavNameByUavCode(@RequestParam("uavCode") String uavCode) {
        return airPlanService.selectUavNameByUavCode(uavCode);
    }

}
