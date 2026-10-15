package com.chinatower.airline.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chinatower.airline.dto.request.AirTaskListReq;
import com.chinatower.airline.dto.request.AirUavStatus;
import com.chinatower.airline.dto.request.InspectionPlanExpectationReq;
import com.chinatower.airline.dto.request.InspectionPlanTriggerReq;
import com.chinatower.airline.dto.response.AirTaskRes;
import com.chinatower.airline.enums.InspectionPlanTypeEnum;
import com.chinatower.airline.mapper.AirPlanMapper;
import com.chinatower.airline.mapper.AirTaskMapper;
import com.chinatower.airline.service.AirPlanService;
import com.chinatower.airline.service.AirResultService;
import com.chinatower.airline.service.AirTaskService;
import com.chinatower.airline.util.DistanceUtils;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.mapper.UavSecureStrategyMapper;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author lize
 * @Date 2024/11/11 20:06
 */
@Service
@Slf4j
public class AirTaskServiceImpl implements AirTaskService {


    @Autowired
    private AirTaskMapper airTaskMapper;

    @Autowired
    private AirPlanMapper airPlanMapper;

    @Autowired
    private UavOkhttpUtil okhttpUtil;
    @Resource
    private UavSecureStrategyMapper uavSecureStrategyMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private AirPlanService airPlanService;
    @Autowired
    private AirResultService airResultService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 根据id删除任务
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public Result deleteById(AirTaskReq airTaskReq) {
        if (StringUtils.isNull(airTaskReq)) {
            return Result.fail("请求不能为空");
        }
        //获取任务的接入节点
        AirTaskRes airTaskRes = airTaskMapper.findById(airTaskReq);
        String accessNode = "";
        if (ObjectUtil.isNotNull(airTaskRes)) {
            accessNode = airTaskRes.getAccessNode();
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //获取节点编码及登录用户名
        String platformCode = user.getPlatformCode();
        String userName = user.getUserName();
        String result = "";
        //调用飞控接口 巡检计划明细删除
        if (ObjectUtil.isNotNull(airTaskReq)) {
            String joIn = String.valueOf(JSONObject.toJSON(airTaskReq));

            NetConnectUtils.RequestParamter<AirTaskReq> paramter = new NetConnectUtils.RequestParamter<>();
            //请求参数
            paramter.setBody(airTaskReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_TASK_DELETE);
            path.setTaskRecordId(airTaskReq.getTaskRecordId());
            paramter.setPath(path);

            try {
                log.info("调用飞控接口巡检计划明细删除入参：" + joIn);
                ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
                log.info("调用飞控接口巡检计划明细删除结果：" + JSON.toJSONString(resultEntity));
                result = resultEntity.getErrorMsg();
                //如果飞控平台删除成功，同步删除通用数据库
                if (ObjectUtil.isNotNull(resultEntity) && Constants.SUCCESS_CODE.equals(resultEntity.getReturnCode())) {
                    result = resultEntity.getReturnMsg();
                } else {
                    return Result.fail(result);
                }
            } catch (Exception e) {
                log.error("调用飞控接口巡检计划明细删除失败：{}", e);
                return Result.fail("调用飞控接口巡检计划明细删除失败");
            }

            if (Constants.TWO.equals(airTaskReq.getDeleteFlag().toString())) {
                airTaskMapper.deleteCollectTask(airTaskReq.getTaskRecordId());
            }

            // 删除计划管理和任务
            //获取该计划下的任务数量
            Integer taskNum = airTaskMapper.findPlanTaskNumByPlanId(airTaskReq.getPlanId());
            airTaskReq.setUpdateUser(userName);
            if (airTaskReq.getDeleteFlag() == null) {
                //没有传递删除标记的默认为假删除
                airTaskReq.setDeleteFlag(1);
            }
            airTaskMapper.deleteById(airTaskReq);
            if (taskNum == 1) {
                log.info("该计划下只有要删除得当前任务-同步删除该计划:{}", JSON.toJSONString(airTaskReq.getPlanId()));
                //删除该计划
                AirPlanAddOrEditReq req = new AirPlanAddOrEditReq();
                req.setPlanId(Integer.valueOf(airTaskReq.getPlanId()));
                //从删除任务过来的标记位
                req.setTaskDeleteFlag(Constants.ZERO);
                airPlanService.deleteByPlanId(req);
                        /*AirPlanRep airPlanRep = new AirPlanRep();
                        airPlanRep.setPlanId(airTaskReq.getPlanId());
                        airPlanRep.setUpdateUser(userName);
                        airPlanMapper.deleteByPlanId(airPlanRep);*/
            }
        }
        return Result.success(result);
    }

    /**
     * 计算巡检计划预期
     *
     * @param trigger 巡检计划触发规则信息
     * @return 巡检计划预期
     */
    public InspectionPlanExpectationReq calcInspectionPlanExpectation(InspectionPlanTriggerReq trigger) {
        //判断计划类型
        Integer planType = trigger.getPlanType();
        if (log.isDebugEnabled()) {
            log.debug("当前巡检计划类型为：{}", planType);
        }
        // 立即执行计划
        if (Objects.equals(planType, InspectionPlanTypeEnum.SOON.ordinal())) {
            return buildSoonInspectionPlan();
        }
        // 定期执行计划
        if (Objects.equals(planType, InspectionPlanTypeEnum.REGULAR.ordinal())) {
            return buildRegularInspectionPlan(trigger);
        }
        // 周期计划
        return buildCycleInspectionPlan(trigger);
    }

    /**
     * 查询任务列表
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public List<AirTaskRes> queryTaskList(AirTaskListReq airTaskReq) {
        Integer pageNum = airTaskReq.getPageNum();
        Integer pageSize = airTaskReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        if (!CollectionUtils.isEmpty(airTaskReq.getStatus())) {
            List<String> status = airTaskReq.getStatus();
            status.stream().forEach(statusItem -> {
                if (Constants.FIVE.equals(statusItem)) {
                    airTaskReq.setIsItEnabled(Constants.ONE);
                }
            });
        }
        return airTaskMapper.queryTaskList(airTaskReq);
    }

    /**
     * 断点续飞
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public Result continueUavTask(AirTaskListReq airTaskReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        NetConnectUtils.RequestParamter<AirTaskListReq> paramter = new NetConnectUtils.RequestParamter<>();
        //请求参数
        paramter.setBody(airTaskReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        //获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_UAV_NEST_STATUS);
        paramter.setPath(path);
        //断点续飞
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, airTaskReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = (JSONObject) resultEntity.getData();
            String nsetStatus = jsonObject.get(com.chinatower.airline.constants.Constants.NEST_STATUS).toString();
            String nestDebug = jsonObject.get(com.chinatower.airline.constants.Constants.NEST_DEBUG).toString();
            //如果飞机处于在线 上锁并执行断点续飞
            if (Constants.ZERO.equals(nsetStatus) && Constants.ZERO.equals(nestDebug)) {
                String cacheKey = com.chinatower.airline.constants.Constants.UAV_TASK_CONTINUE_KEY + airTaskReq.getTaskRecordId();
                boolean lock = RedisUtil.setIfAbsent(cacheKey, com.chinatower.airline.constants.Constants.LOCK, 120, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                if (lock) {
                    NetConnectUtils.RequestParamter<AirTaskListReq> paramter2 = new NetConnectUtils.RequestParamter<>();
                    //请求参数
                    paramter2.setBody(airTaskReq);
                    NetConnectUtils.RequestParamter.Path path2 = new NetConnectUtils.RequestParamter.Path();
                    //获取接口地址
                    path2.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_CONTINUE_TASK);
                    paramter2.setPath(path2);
                    //断点续飞
                    ResultEntity resultEntity2 = okhttpUtil.doPostUavGateWay(paramter2, airTaskReq.getAccessNode(), platformCode);
                    if (ObjectUtil.isNotNull(resultEntity2) && ObjectUtil.isNotNull(resultEntity2.getData())) {
                        return Result.data(resultEntity2.getData());
                    }
                }
            }
        }
        return Result.fail(com.chinatower.airline.constants.Constants.UAV_TASK_ERROR_JSON);
    }

    /**
     * 查询任务状态
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public Result checkUavTaskStatus(AirTaskListReq airTaskReq) {
        List<String> deviceCodes = airTaskReq.getDeviceCodes();
        //查询mongo 无人机设备的状态
        // 创建聚合操作
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, Constants.PARAM_CREATE_TIME);
        GroupOperation groupOperation = Aggregation.group(Constants.PARAM_DEVICE_CODE)
                .first(Constants.PARAM_DEVICE_CODE).as(Constants.PARAM_DEVICE_CODE) // 显式提取 deviceCode
                .first("runStatus").as("runStatus") // 显式提取 deviceCode
                .first(Constants.PARAM_CREATE_TIME).as(Constants.PARAM_CREATE_TIME) // 显式提取 deviceCode
                .first("$$ROOT").as("latestStatus");
        TypedAggregation<AirUavStatus> aggregation = Aggregation.newAggregation(
                AirUavStatus.class,
                Aggregation.match(Criteria.where(Constants.PARAM_DEVICE_CODE).in(airTaskReq.getDeviceCodes())),
                sortOperation,
                groupOperation
        );
        // 执行聚合查询
        AggregationResults<AirUavStatus> results = mongoTemplate.aggregate(aggregation, Constants.UAV_SYNC_STATUS, AirUavStatus.class);
        // 获取结果
        List<AirUavStatus> airUavStatusesRsp = new ArrayList<>();
        List<AirUavStatus> airUavStatuses = results.getMappedResults();
        if (CollectionUtils.isEmpty(airUavStatuses)) {
            deviceCodes.forEach(deviceCode -> {
                if (CollectionUtils.isEmpty(airUavStatuses)) {
                    AirUavStatus airUavStatus = new AirUavStatus();
                    airUavStatus.setDeviceCode(deviceCode);
                    airUavStatus.setRunStatus(Integer.parseInt(Constants.ZERO));
                    airUavStatuses.stream().forEach(airUavStatu -> {
                        if (Objects.equals(airUavStatu.getDeviceCode(), deviceCode)) {
                            airUavStatus.setRunStatus(airUavStatu.getRunStatus());
                        }
                    });
                    airUavStatusesRsp.add(airUavStatus);
                }
            });
        }
        return Result.data(airUavStatuses);
    }

    /**
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result queryReFlyName(AirResultReq airResultReq) {
        if (StringUtils.isNotEmpty(airResultReq.getOriginalTaskRecordId())) {
            airResultReq.setTaskRecordId(airResultReq.getOriginalTaskRecordId());
        }
        AirTaskRes taskRes = airTaskMapper.queryTaskInfo(airResultReq.getTaskRecordId());
        int count = airTaskMapper.queryTaskInfoCount(airResultReq.getTaskRecordId());
        return Result.data(taskRes.getPlanName() + "复飞任务_" + count);
    }

    /**
     * * 查询天气策略
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result queryWeatherStrategy(AirResultReq airResultReq) {
        //根据任务信息查询租户行业以及无人机编码
        AirTaskRes airTaskRes = airTaskMapper.queryTaskInfo(airResultReq.getTaskRecordId());
        if (ObjectUtil.isEmpty(airTaskRes)) {
            return Result.data(new HashMap<>());
        }
        //安全策略
        UavDeviceDTO param = new UavDeviceDTO();
        param.setDeviceCode(airTaskRes.getUavCode());
        param.setTenantId(airTaskRes.getTenantId());
        param.setIndustryCode(airTaskRes.getIndustryCode());
        List<VmUavSecureStrategyDTO> uavSecureStrategyDTOS = uavSecureStrategyMapper.queryVmUavSecureStrategyByUavTask(param);
        if (CollectionUtil.isNotEmpty(uavSecureStrategyDTOS)) {
            uavSecureStrategyDTOS.stream().forEach(uavSecureStrategyDTO -> {
                VmUavSecureStrategyDTO vmUavSecureStrategyDTO = new VmUavSecureStrategyDTO();
                vmUavSecureStrategyDTO.setMinValue(uavSecureStrategyDTO.getMinValue());
                vmUavSecureStrategyDTO.setMaxValue(uavSecureStrategyDTO.getMaxValue());
                if (Objects.equals(uavSecureStrategyDTO.getMinSymbol(), Constants.OPERATOR_LESS)) {
                    vmUavSecureStrategyDTO.setMinSymbol(Constants.ZERO);
                }
                if (Objects.equals(uavSecureStrategyDTO.getMinSymbol(), Constants.OPERATOR_LE)) {
                    vmUavSecureStrategyDTO.setMinSymbol(Constants.ONE);
                }
                if (Objects.equals(uavSecureStrategyDTO.getMaxSymbol(), Constants.OPERATOR_LESS)) {
                    vmUavSecureStrategyDTO.setMaxSymbol(Constants.ZERO);
                }
                if (Objects.equals(uavSecureStrategyDTO.getMaxSymbol(), Constants.OPERATOR_LE)) {
                    vmUavSecureStrategyDTO.setMaxSymbol(Constants.ONE);
                }
            });
        }
        return Result.data(uavSecureStrategyDTOS);
    }

    /**
     * 查询任务列表
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public List<AirTaskRes> queryTaskListLimit(AirTaskListReq airTaskReq) {
        if (ObjectUtil.isEmpty(airTaskReq)) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(airTaskReq.getUavCode())) {
            return new ArrayList<>();
        }
        Integer pageNum = airTaskReq.getPageNum();
        Integer pageSize = airTaskReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        airTaskReq.setIndustryCode(industryCode);
        airTaskReq.setTenantId(tenantId);
        List<AirTaskRes> airTaskResList = airTaskMapper.queryTaskListLimit(airTaskReq);
        log.info("queryTaskListLimit-result:{}", JSONObject.toJSON(airTaskResList));
        return airTaskResList;
    }

    /**
     * 查询
     *
     * @param airTaskReq
     * @return
     */
    @Override
    public Result checkUavStatus(AirTaskListReq airTaskReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        NetConnectUtils.RequestParamter<AirTaskListReq> paramter = new NetConnectUtils.RequestParamter<>();
        //请求参数
        paramter.setBody(airTaskReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        //获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_UAV_NEST_STATUS);
        paramter.setPath(path);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWayNoException(paramter, airTaskReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = (JSONObject) resultEntity.getData();
            String nsetStatus = jsonObject.get(com.chinatower.airline.constants.Constants.NEST_STATUS).toString();
            String nestDebug = jsonObject.get(com.chinatower.airline.constants.Constants.NEST_DEBUG).toString();
            if (Constants.ZERO.equals(nsetStatus) && Constants.ZERO.equals(nestDebug)) {
                return Result.data(true);
            } else {
                return Result.data(false);
            }
        } else {
            if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getErrorMsg())) {
                return Result.data(false, resultEntity.getErrorMsg());
            } else {
                return Result.data(false);
            }
        }
    }

    @Override
    public Result updateTaskAccumulateMileage(AirTaskReq airTaskReq) {
        int i = airTaskMapper.updateTaskAccumulateMileage(airTaskReq);
        return Result.data(i);
    }

    /**
     * 立即执行时间计算
     *
     * @return
     */
    private InspectionPlanExpectationReq buildSoonInspectionPlan() {
        LocalDateTime executionDate = LocalDateTime.now().plusSeconds(3);
        InspectionPlanExpectationReq planExpectation = new InspectionPlanExpectationReq();
        planExpectation.setEstimatedExecTime(executionDate.toLocalTime());
        planExpectation.setEstimatedExecDates(Collections.singletonList(executionDate.toLocalDate()));
        return planExpectation;
    }

    /**
     * 定期执行时间计算
     *
     * @param trigger
     * @return
     */
    private InspectionPlanExpectationReq buildRegularInspectionPlan(InspectionPlanTriggerReq trigger) {
        LocalDateTime firstExecDateTime = LocalDateTime.now().plusMinutes(5L);
        LocalDateTime executionDate = trigger.getRegularExecutionDate();
        if (firstExecDateTime.isAfter(executionDate)) {
            throw new CustomException("定期计划执行时间限制在当前时间5分钟之后");
        }
        InspectionPlanExpectationReq planExpectation = new InspectionPlanExpectationReq();
        planExpectation.setEstimatedExecTime(executionDate.toLocalTime());
        planExpectation.setEstimatedExecDates(Collections.singletonList(executionDate.toLocalDate()));
        return planExpectation;
    }

    /**
     * 周期执行时间计算
     *
     * @param trigger
     * @return
     */
    private InspectionPlanExpectationReq buildCycleInspectionPlan(InspectionPlanTriggerReq trigger) {
        // 周期任务
        LocalTime cycleExecutionTime = trigger.getCycleExecutionTime();
        if (Objects.isNull(cycleExecutionTime)) {
            throw new CustomException("周期计划未设置执行时间");
        }

        Integer cycleExecutionUnit = trigger.getCycleExecutionUnit();
        if (log.isDebugEnabled()) {
            log.debug("当前巡检计划周期执行单位为：{}", cycleExecutionUnit);
        }

        // 周期计划:按天执行
        if (Objects.equals(cycleExecutionUnit, "0")) {
            return buildCycleInspectionPlanDaily(trigger);
        }

        // 周期计划:按周执行
        if (Objects.equals(cycleExecutionUnit, "1")) {
            return buildInspectionPlanWeekly(trigger);
        }

        // 周期计划:按月执行
        return buildInspectionPlanMonthly(trigger);
    }

    /**
     * 周期计划--按天执行
     *
     * @param trigger
     * @return
     */
    private InspectionPlanExpectationReq buildCycleInspectionPlanDaily(InspectionPlanTriggerReq trigger) {
        LocalDateTime startTime = trigger.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        if (ObjectUtil.isNotNull(startTime)) {
            now = startTime;
        }
        LocalDate startDate = now.toLocalDate();
        LocalDate endDate = getTriggerEndDate(now, trigger.getEndTime());
        LocalTime firstExecTime = getFirstExecTime(now.toLocalTime());
        Integer dayInterval = trigger.getDayInterval();
        if (Objects.isNull(dayInterval)) {
            log.info("周期计划，按天执行，未设置执行间隔，使用默认间隔值：1天");
            dayInterval = 1;
        }
        List<LocalDate> estimatedDates = new ArrayList<>();
        LocalTime cycleExecutionTime = trigger.getCycleExecutionTime();
        if (firstExecTime.isBefore(cycleExecutionTime)) {
            estimatedDates.add(startDate);
        }
        startDate = startDate.plusDays(dayInterval);
        while (startDate.isBefore(endDate)) {
            estimatedDates.add(startDate);
            startDate = startDate.plusDays(dayInterval);
        }
        InspectionPlanExpectationReq ipExpectation = new InspectionPlanExpectationReq();
        ipExpectation.setEstimatedExecTime(cycleExecutionTime);
        ipExpectation.setEstimatedExecDates(estimatedDates);
        return ipExpectation;
    }

    /**
     * 周期计划--按周执行
     *
     * @param trigger
     * @return
     */
    private InspectionPlanExpectationReq buildInspectionPlanWeekly(InspectionPlanTriggerReq trigger) {
        LocalDateTime startTime = trigger.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        if (ObjectUtil.isNotNull(startTime)) {
            now = startTime;
        }
        LocalDate startDate = now.toLocalDate();
        LocalDate endDate = getTriggerEndDate(now, trigger.getEndTime());
        LocalTime firstExecTime = getFirstExecTime(now.toLocalTime());
        Set<Integer> weekDays = trigger.getIntervalValues().stream().filter(e -> e >= 1 & e <= 7).collect(Collectors.toSet());
        List<LocalDate> estimatedDates = new ArrayList<>();
        LocalTime cycleExecutionTime = trigger.getCycleExecutionTime();
        if (weekDays.contains(startDate.getDayOfWeek().getValue()) && firstExecTime.isBefore(cycleExecutionTime)) {
            estimatedDates.add(startDate);
        }
        startDate = startDate.plusDays(1);
        while (startDate.isBefore(endDate)) {
            if (weekDays.contains(startDate.getDayOfWeek().getValue())) {
                estimatedDates.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }
        InspectionPlanExpectationReq ipExpectation = new InspectionPlanExpectationReq();
        ipExpectation.setEstimatedExecTime(cycleExecutionTime);
        ipExpectation.setEstimatedExecDates(estimatedDates);
        return ipExpectation;
    }

    /**
     * 周期计划--按月执行
     *
     * @param trigger
     * @return
     */
    private InspectionPlanExpectationReq buildInspectionPlanMonthly(InspectionPlanTriggerReq trigger) {
        LocalDateTime startTime = trigger.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        if (ObjectUtil.isNotNull(startTime)) {
            now = startTime;
        }
        LocalDate startDate = now.toLocalDate();
        LocalDate endDate = getTriggerEndDate(now, trigger.getEndTime());
        LocalTime firstExecTime = getFirstExecTime(now.toLocalTime());
        LocalTime cycleExecutionTime = trigger.getCycleExecutionTime();
        Set<Integer> monthDays = trigger.getIntervalValues().stream().filter(e -> e >= 1 & e <= 31).collect(Collectors.toSet());
        List<LocalDate> estimatedDates = new ArrayList<>();
        if (monthDays.contains(startDate.getDayOfMonth()) && firstExecTime.isBefore(cycleExecutionTime)) {
            estimatedDates.add(startDate);
        }
        startDate = startDate.plusDays(1);
        while (startDate.isBefore(endDate)) {
            if (monthDays.contains(startDate.getDayOfMonth())) {
                estimatedDates.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }
        InspectionPlanExpectationReq ipExpectation = new InspectionPlanExpectationReq();
        ipExpectation.setEstimatedExecTime(cycleExecutionTime);
        ipExpectation.setEstimatedExecDates(estimatedDates);
        return ipExpectation;
    }

    private LocalDate getTriggerEndDate(LocalDateTime today, LocalDateTime triggerEndDate) {
        if (Objects.isNull(triggerEndDate) || today.isAfter(triggerEndDate)) {
            triggerEndDate = today.plusMonths(1L);
        }
        return triggerEndDate.toLocalDate().plusDays(1);
    }

    private LocalTime getFirstExecTime(LocalTime nowTime) {
        return nowTime.plusMinutes(5L);
    }


    /**
     * 从历史记录获取实际飞行里程（累计里程）
     *
     * @param taskRecordId 任务ID
     * @return 实际飞行里程（单位：米）
     */
    private String getActualAirMileageFromHistory(String taskRecordId) {
        try {
            AirHistoryReq airHistoryReq = new AirHistoryReq();
            airHistoryReq.setTaskRecordId(taskRecordId);
            List<UavFlyReq> uavFlyReqs = airResultService.queryHisRecordLimit(airHistoryReq);
            // 计算飞行距离
            if (CollectionUtils.isNotEmpty(uavFlyReqs)) {
                // 按时间戳排序，确保轨迹点按时间顺序
                uavFlyReqs.sort((a, b) -> {
                    if (a == null || a.getTimestamp() == null) return 1;
                    if (b == null || b.getTimestamp() == null) return -1;
                    return a.getTimestamp().compareTo(b.getTimestamp());
                });
                calculateFlightDistance(uavFlyReqs);
                // 获取最后一个记录（最新的记录）
                UavFlyReq lastRecord = uavFlyReqs.get(uavFlyReqs.size() - 1);
                Double flightDistance = lastRecord.getFlightDistance();
                if (flightDistance != null) {
                    return String.valueOf(flightDistance);
                }
            }
        } catch (Exception e) {
            log.warn("任务[{}]从历史记录获取实际飞行里程异常", taskRecordId, e);
        }
        return null;
    }


    /**
     * 计算飞行轨迹的累计飞行距离
     * 根据轨迹点列表，计算从起点到每个点的累计飞行距离
     *
     * @param uavFlyReqs 飞行轨迹点列表，需要按时间排序
     */
    private void calculateFlightDistance(List<UavFlyReq> uavFlyReqs) {
        if (org.springframework.util.CollectionUtils.isEmpty(uavFlyReqs)) {
            return;
        }

        double totalDistance = 0.0;
        // 第一个点的飞行距离为0
        if (uavFlyReqs.get(0) != null) {
            uavFlyReqs.get(0).setFlightDistance(0.0);
        }

        // 从第二个点开始，计算累计距离
        for (int i = 1; i < uavFlyReqs.size(); i++) {
            UavFlyReq current = uavFlyReqs.get(i);
            UavFlyReq previous = uavFlyReqs.get(i - 1);

            // 检查经纬度是否有效
            if (current != null && previous != null
                    && StringUtils.isNotEmpty(current.getLng()) && StringUtils.isNotEmpty(current.getLat())
                    && StringUtils.isNotEmpty(previous.getLng()) && StringUtils.isNotEmpty(previous.getLat())) {
                try {
                    // 计算两点之间的距离
                    double segmentDistance = DistanceUtils.calculateDistance(
                            previous.getLng(), previous.getLat(),
                            current.getLng(), current.getLat()
                    );
                    // 累加距离
                    totalDistance += segmentDistance;
                    // 设置当前点的累计飞行距离
                    if (current != null) {
                        current.setFlightDistance(new BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    }
                } catch (Exception e) {
                    log.warn("计算飞行距离异常，跳过该点: {}", e.getMessage());
                    // 如果计算失败，使用前一个点的距离
                    if (current != null && previous != null) {
                        current.setFlightDistance(previous.getFlightDistance() != null ? previous.getFlightDistance() : 0.0);
                    }
                }
            } else {
                // 如果经纬度无效，使用前一个点的距离
                if (current != null) {
                    if (previous != null && previous.getFlightDistance() != null) {
                        current.setFlightDistance(previous.getFlightDistance());
                    } else {
                        current.setFlightDistance(0.0);
                    }
                }
            }
        }
    }

    /**
     * 批量计算任务实际里程
     * 先从 Redis 获取，获取不到再从历史轨迹中计算并缓存
     *
     * @param taskRecordIds 任务记录ID列表
     * @return Map<taskRecordId, 实际里程>，里程单位为米
     */
    @Override
    public Map<String, String> batchCalculateActualMileage(List<String> taskRecordIds) {
        if (CollectionUtils.isEmpty(taskRecordIds)) {
            return new HashMap<>();
        }

        // 去重
        List<String> uniqueTaskRecordIds = taskRecordIds.stream()
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(uniqueTaskRecordIds)) {
            return new HashMap<>();
        }

        log.info("开始批量计算任务实际里程，任务数量: {}", uniqueTaskRecordIds.size());

        // 先从 Redis 获取已缓存的数据
        Map<String, String> resultMap = new HashMap<>();
        List<String> needCalculateIds = new ArrayList<>();

        for (String taskRecordId : uniqueTaskRecordIds) {
            String cacheKey = getCacheKey(taskRecordId);
            String cachedMileage = RedisUtil.get(cacheKey, RedisDBType.PRIMARY);
            if (StringUtils.isNotEmpty(cachedMileage)) {
                resultMap.put(taskRecordId, cachedMileage);
                log.debug("从缓存获取任务[{}]的实际里程: {}", taskRecordId, cachedMileage);
            } else {
                needCalculateIds.add(taskRecordId);
            }
        }

        // 如果所有数据都在缓存中，直接返回
        if (CollectionUtils.isEmpty(needCalculateIds)) {
            log.info("所有任务的实际里程都从缓存获取，无需计算");
            return resultMap;
        }

        log.info("需要计算的任务数量: {}", needCalculateIds.size());

        // 多线程计算
        Map<String, String> calculatedMap = calculateMileageWithMultiThread(needCalculateIds);

        // 合并结果并缓存
        resultMap.putAll(calculatedMap);
        calculatedMap.forEach((taskRecordId, mileage) -> {
            if (StringUtils.isNotEmpty(mileage)) {
                String cacheKey = getCacheKey(taskRecordId);
                RedisUtil.set(cacheKey, mileage, RedisDBType.PRIMARY);
                RedisUtil.expire(cacheKey, com.chinatower.airline.constants.Constants.CACHE_EXPIRE_DAYS, TimeUnit.DAYS, RedisDBType.PRIMARY);
                log.debug("任务[{}]的实际里程已缓存: {}", taskRecordId, mileage);
            }
        });

        log.info("批量计算任务实际里程完成，总数量: {}, 缓存命中: {}, 新计算: {}",
                uniqueTaskRecordIds.size(),
                uniqueTaskRecordIds.size() - needCalculateIds.size(),
                needCalculateIds.size());

        return resultMap;
    }

    @Override
    public Result terminateTask(AirTaskReq airTaskReq) {
        NetConnectUtils.RequestParamter<AirTaskReq> parameter = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TERMINATE_TASK);
        parameter.setPath(path);
        parameter.setBody(airTaskReq);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(parameter, null, null);
        if (resultEntity == null || resultEntity.getData() == null) {
            return Result.fail("终止任务失败");
        }
        if(resultEntity.getReturnCode().equals("0000")){  //返回成功
            airTaskMapper.updateTasksStatus(airTaskReq.getTaskRecordId());
        }else{
            return Result.fail(resultEntity.getReturnMsg());
        }
        return Result.data(resultEntity.getData());
    }

    /**
     * 多线程计算里程
     * 如果任务数量大于10，分成3组线程计算
     *
     * @param taskRecordIds 需要计算的任务ID列表
     * @return 计算结果 Map
     */
    private Map<String, String> calculateMileageWithMultiThread(List<String> taskRecordIds) {
        Map<String, String> resultMap = new ConcurrentHashMap<>();

        // 多线程分组阈值：任务数量小于等于10时，直接多线程计算；大于10时，分成3组
        int THREAD_GROUP_THRESHOLD = 10;

        // 如果任务数量小于等于阈值，直接多线程计算
        if (taskRecordIds.size() <= THREAD_GROUP_THRESHOLD) {
            executeTasksInParallel(taskRecordIds, resultMap);
        } else {
            // 分成3组
            int groupSize = (taskRecordIds.size() + 2) / 3; // 向上取整
            List<List<String>> groups = new ArrayList<>();
            for (int i = 0; i < taskRecordIds.size(); i += groupSize) {
                int end = Math.min(i + groupSize, taskRecordIds.size());
                groups.add(taskRecordIds.subList(i, end));
            }

            log.info("任务数量大于10，分成{}组进行多线程计算", groups.size());

            // 每组并行计算
            List<CompletableFuture<Void>> groupFutures = groups.stream()
                    .map(group -> CompletableFuture.runAsync(() -> {
                        executeTasksInParallel(group, resultMap);
                    }, threadPoolTaskExecutor))
                    .collect(Collectors.toList());

            // 等待所有组完成
            CompletableFuture.allOf(groupFutures.toArray(new CompletableFuture[0])).join();
        }

        return resultMap;
    }

    /**
     * 并行执行任务列表，计算实际里程
     *
     * @param taskRecordIds 任务记录ID列表
     * @param resultMap 结果 Map，用于存储计算结果
     */
    private void executeTasksInParallel(List<String> taskRecordIds, Map<String, String> resultMap) {
        List<CompletableFuture<Void>> futures = taskRecordIds.stream()
                .map(taskRecordId -> CompletableFuture.runAsync(() -> {
                    try {
                        String mileage = getActualAirMileageFromHistory(taskRecordId);
                        if (StringUtils.isNotEmpty(mileage)) {
                            resultMap.put(taskRecordId, mileage);
                        }
                    } catch (Exception e) {
                        log.error("计算任务[{}]实际里程异常", taskRecordId, e);
                    }
                }, threadPoolTaskExecutor))
                .collect(Collectors.toList());

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 获取 Redis 缓存 key
     *
     * @param taskRecordId 任务记录ID
     * @return 缓存 key
     */
    private String getCacheKey(String taskRecordId) {
        return com.chinatower.airline.constants.Constants.REDIS_KEY_PREFIX + taskRecordId;
    }
}
