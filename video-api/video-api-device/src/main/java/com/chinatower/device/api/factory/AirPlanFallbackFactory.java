package com.chinatower.device.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirPlanService;
import com.chinatower.device.entity.dto.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 降级处理
 */
@Component
public class AirPlanFallbackFactory implements FallbackFactory<IAirPlanService> {

    @Override
    public IAirPlanService create(Throwable throwable) {
        return new IAirPlanService() {
            @Override
            public Result findByCondition(AirPlanPageReq vo) {
                return Result.fail("查询计划管理列表失败:" + throwable.getMessage());
            }

            @Override
            public Result findAirPlanById(Integer planId) {
                return Result.fail("根据id查询计划管理详情失败:" + throwable.getMessage());
            }

            @Override
            public Result addAirPlan(AirPlanAddOrEditReq vo) {
                return Result.fail("新增计划管理失败:" + throwable.getMessage());
            }

            @Override
            public Result editAirPlan(AirPlanAddOrEditReq vo) {
                return Result.fail("编辑计划管理失败:" + throwable.getMessage());
            }

            @Override
            public Result deleteByPlanId(AirPlanAddOrEditReq vo) {
                return Result.fail("删除计划管理失败:" + throwable.getMessage());
            }

            @Override
            public Result getPlanName(String tenantId, String industryCode) {
                return Result.fail("获取自动生成的计划名称失败:" + throwable.getMessage());
            }

            @Override
            public Result planNameIsRepeat(String tenantId, String industryCode, String planName, Integer id) {
                return Result.fail("校验计划名称是否唯一失败:" + throwable.getMessage());
            }

            @Override
            public Result copyPlanName(String planId, String tenantId, String industryCode) {
                return Result.fail("获取生成的复制计划名称失败:" + throwable.getMessage());
            }

            @Override
            public Result updateIsItEnabled(AirTaskEnableReq airTaskReq) {
                return Result.fail("巡检计划明细启用或关闭失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<String>> querySafetyOfficerPhonesByFlyingUav(String uavCode) {
                return Result.fail("HMS 飞行告警短信：根据无人机编码查询当前飞行计划的安全员手机号（去重）失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> queryUavNameByUavCode(String uavCode) {
                return Result.fail("HMS 飞行告警短信：根据无人机编码查询无人机名称失败:" + throwable.getMessage());
            }

            @Override
            public Result deleteById(AirTaskReq airTaskReq) {
                return Result.fail("根据id删除计划下任务失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryTaskList(AirTaskListReq airTaskReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result continueUavTask(AirTaskListReq airTaskReq) {
                return Result.fail("断点续飞失败:" + throwable.getMessage());
            }

            @Override
            public Result checkUavStatus(AirTaskListReq airTaskReq) {
                return Result.fail("校验无人机状态失败:" + throwable.getMessage());
            }

            @Override
            public Result checkUavTaskStatus(AirTaskListReq airTaskReq) {
                return Result.fail("检查无人机状态失败:" + throwable.getMessage());
            }

            @Override
            public Result queryReFlyName(AirResultReq airResultReq) {
                return Result.fail("查询一键复飞名称失败:" + throwable.getMessage());
            }

            @Override
            public Result queryWeatherStrategy(AirResultReq airResultReq) {
                return Result.fail("查询任务天气策略失败:" + throwable.getMessage());
            }
        };
    }
}
