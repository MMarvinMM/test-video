package com.chinatower.airline.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.request.AirPlanSafetyOfficer;
import com.chinatower.airline.dto.response.AirPlanRep;
import com.chinatower.airline.dto.response.AirTaskRes;
import com.chinatower.airline.enums.TaskStrategyEnum;
import com.chinatower.airline.mapper.AirLineMapper;
import com.chinatower.airline.mapper.AirPlanMapper;
import com.chinatower.airline.mapper.AirPlanSafetyOfficerMapper;
import com.chinatower.airline.mapper.AirTaskMapper;
import com.chinatower.airline.service.AirPlanService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author lize
 * @Date 2024/11/4 10:00
 */
@Service
@Slf4j
public class AirPlanServiceImpl implements AirPlanService {

    /**
     * 用户记忆缓存：安全员信息
     * <p>
     * key = prefix + industryCode + ":" + tenantId + ":" + userId + ":" + type
     * value = 安全员列表（去重后最新 10 条）
     */
    private static final String REDIS_USER_MEMORY_PREFIX = "uav:user:memory:";
    private static final String USER_MEMORY_TYPE_SAFETY_OFFICER = "safetyOfficer";

    @Autowired
    private AirPlanMapper airPlanMapper;

    @Resource
    private AirTaskMapper airTaskMapper;

    @Resource
    private UavOkhttpUtil okhttpUtil;

    @Resource
    private AirLineMapper airLineMapper;

    @Resource
    private AirPlanSafetyOfficerMapper airPlanSafetyOfficerMapper;


    //0-待执行
    private static String PENDINGEXECUTION = "pendingExecution";
    //1-执行成功
    private static String EXECUTIONSUCCEEDED = "executionSucceeded";
    //2-执行失败
    private static String EXECUTIONFAILED = "executionFailed";
    //3-执行中
    private static String INPROGRESS = "inProgress";

    private static String UNKNOWNSTATUS = "unknownStatus";

    /**
     * 计划管理列表查询
     *
     * @param vo
     * @return
     */
    @Override
    public TableDataInfo findByCondition(AirPlanPageReq vo) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        Integer pageNum = vo.getPageNum();
        Integer pageSize = vo.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(vo.getStatus())) {
            List<String> status = vo.getStatus();
            status.stream().forEach(statusItem -> {
                if (Constants.FIVE.equals(statusItem)) {
                    vo.setIsItEnabled(Constants.ONE);
                }
            });
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String userId = sysUser.getUserId();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        vo.setUserId(userId);
        vo.setIndustryCode(industryCode);
        vo.setTenantId(tenantId);
        List<AirPlanRep> list = airPlanMapper.airPlanList(vo);
        List<AirTaskReq> resList = new ArrayList<>();
        //查询任务
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> planIds = list.stream().map(AirPlanRep::getPlanId).collect(Collectors.toList());
            vo.setPlanIds(planIds);
            List<AirTaskReq> airTask = airPlanMapper.airTask(vo);
            if (CollectionUtil.isNotEmpty(airTask)) {
                list.stream().forEach(planRep -> {
                    planRep.setChildren(airTask.stream().filter(task1 -> task1.getPlanId().equals(planRep.getPlanId())).collect(Collectors.toList()));
                });
            }
            //获取航线下最新任务中的实际飞行时间
            resList = airPlanMapper.getRealityTimeList(vo);
        }
        List<AirTaskReq> finalResList = resList;
        list.stream().forEach(airPlan -> {
            if (!airPlan.getChildren().isEmpty()) {
                HashMap<String, Integer> map = new HashMap<>();
                airPlan.getChildren().forEach(task -> {
                    if (airPlan.getPlanId().equals(task.getPlanId())) {
                        if (StringUtils.isNotEmpty(task.getTaskType()) && StringUtils.isNotEmpty(airPlan.getTaskType()) && Constants.ZERO.equals(airPlan.getTaskType())) {
                            airPlan.setTaskType(task.getTaskType());
                        }
                        //判断任务策略是否是 重复定时
                        if (airPlan.getPlanType() == TaskStrategyEnum.REPETITIVE_TIMING.getCode()) {
                            //计划执行时间
                            airPlan.setScheduleExecTime("-");
                            //实际执行时间
                            airPlan.setRealityTime("-");
                        } else {
                            airPlan.setScheduleExecTime(task.getScheduleExecTime());
                            airPlan.setRealityTime(task.getRealityTime());


                            if (Constants.ONE.equals(task.getStatus()) || Constants.TWO.equals(task.getStatus())) {
                                airPlan.setUavCode(task.getUavCode());
                                airPlan.setDeviceName(task.getDeviceName());
                            }

                        }
                        BeanUtils.copyProperties(airPlan, task, "id", "status", "mediumStatus", "planName",
                                "isItEnabled", "taskType", "deviceName", "uavCode", "uavNestCode");

                        //如果任务设备名称为空，则从计获取
                        if (StringUtils.isEmpty(task.getDeviceName())) {
                            task.setDeviceName(airPlan.getDeviceName());
                        }

                        //获取计划下的任务状态的个数
                        if (StringUtils.isNotEmpty(task.getStatus())) {
                            // 更新任务状态计数
                            String statusKey = getStatusKey(Integer.valueOf(task.getStatus()));  // 获取任务状态对应的key
                            map.put(statusKey, map.getOrDefault(statusKey, 0) + 1);  // 计数更新
                        }
                        // 将计划表的自动断点续飞字段set到任务子集里
                        task.setAutoResume(airPlan.getAutoResume());
                    }
                });
                //任务状态个数
                airPlan.setExecutionStatesCount(map);
//                //计划任务
//                airPlan.setChildren(taskList);
            }
            if (!CollectionUtils.isEmpty(finalResList)) {
                finalResList.forEach(item -> {
                    if (airPlan.getPlanId().equals(item.getPlanId())) {
                        airPlan.setLastExcTime(item.getRealityTime());
                    }
                });
            }
        });

        // 获取总记录数，这里会触发一次额外的 count 查询
        PageInfo<AirPlanRep> pageInfo = new PageInfo<>(list);
        //设置数据和消息
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(pageInfo.getTotal());
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setCode(HttpStatus.SUCCESS);

        return tableDataInfo;
    }

    private String getStatusKey(Integer status) {
        switch (status) {
            case 0:
                return PENDINGEXECUTION;
            case 1:
                return EXECUTIONSUCCEEDED;
            case 2:
                return EXECUTIONFAILED;
            case 3:
                return INPROGRESS;
            case 4:
                return EXECUTIONFAILED;
            default:
                return UNKNOWNSTATUS;
        }
    }

    /**
     * 根据id查询详情
     *
     * @param planId
     * @return
     */
    @Override
    public AirPlanRep findAirPlanById(Integer planId) {
        if (StringUtils.isNull(planId)) {
            throw new CustomException("planId为空");
        }
        AirPlanRep airPlanRep = airPlanMapper.findAirPlanById(planId);
        if (ObjectUtil.isNotNull(airPlanRep) && ObjectUtil.isNotNull(airPlanRep.getCycleExecutionTime())) {
            String cycleExecutionTime = airPlanRep.getCycleExecutionTime();
            String[] cycleExecutionTimesArray = cycleExecutionTime.split(",");
            List<String> cycleExecutionTimesList = Arrays.asList(cycleExecutionTimesArray);
            airPlanRep.setCycleExecutionTimes(cycleExecutionTimesList);
        }
        // 将计划表的自动断点续飞字段set到任务子集里
        if (airPlanRep != null && airPlanRep.getChildren() != null) {
            airPlanRep.getChildren().forEach(task -> task.setAutoResume(airPlanRep.getAutoResume()));
        }
        List<AirPlanSafetyOfficer> airPlanSafetyOfficers = airPlanSafetyOfficerMapper.selectByPlanId(Long.valueOf(planId));
        airPlanRep.setSafetyOfficers(airPlanSafetyOfficers);
        return airPlanRep;
    }

    /**
     * 新增/编辑计划管理
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public Result addOrEditAirPlan(AirPlanAddOrEditReq vo, String optType) {
        log.info("飞行计划创建入参{}", JSONObject.toJSON(vo));
        //查询航线快照
        AirEditDto airEditDto = new AirEditDto();
        airEditDto.setFlightRouteId(vo.getFlightRouteId());
        AirLineVo airLine = airLineMapper.getAirLineInfo(airEditDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String industryCode = loginUser.getUser().getIndustryCode();
        String appVerCode = loginUser.getUser().getAppVerCode();
        String platformCode = loginUser.getUser().getPlatformCode();
        String tenantId = loginUser.getUser().getTenantId();
        vo.setAcceptNode(platformCode);
        vo.setBreakPoint(true);
        // 默认关闭自动断点续飞
        if (ObjectUtil.isEmpty(vo.getAutoResume())) {
            vo.setAutoResume(false);
        }
        vo.setVideoQuality(vo.getDefinition());
        //调用飞控接口 新增/编辑计划管理
        NetConnectUtils.RequestParamter<AirPlanAddOrEditReq> paramter = new NetConnectUtils.RequestParamter<>();
        paramter.setBody(vo);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_ADD_AND_EDIT);
        paramter.setPath(path);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, vo.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject data = (JSONObject) resultEntity.getData();
            //判断新增还是编辑计划管理
            boolean addOrEdit = false;
            if (ObjectUtil.isEmpty(vo.getPlanId())) {
                addOrEdit = true;
            }
            log.info("新增飞控计划调用飞控接口返回：{}", data);
            String planId = data.getString("planId");
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(data.get("recordList")));
            vo.setPlanId(Integer.valueOf(planId));
            String userName = SecurityUtils.getActualLoginUser().getUsername();
            vo.setIndustryCode(industryCode);
            vo.setAcceptNode(platformCode);
            vo.setAccessNode(vo.getAccessNode());
            vo.setTenantId(tenantId);
            vo.setAppVerCode(appVerCode);
            AirPlanRep airPlan = new AirPlanRep();
            BeanUtils.copyProperties(vo, airPlan);
            airPlan.setSnapshotId(airLine.getSnapshotId());
            airPlan.setPlanId(planId);
            airPlan.setPlanName(vo.getName());
            airPlan.setPlanStartTime(vo.getStartTime());
            airPlan.setPlanEndTime(vo.getEndTime());
            airPlan.setIsItEnabled("0");
            airPlan.setDefinition(vo.getDefinition());
            airPlan.setCycleExecutionUnit(vo.getCycleExecutionUnit().toString());
            airPlan.setRiskMsgSwitch(vo.getRiskMsgSwitch());
            if (ObjectUtil.isNotNull(vo.getCycleExecutionTimes())) {
                List<String> cycleExecutionTimes = vo.getCycleExecutionTimes();
                String joinedString = cycleExecutionTimes.stream()
                        .collect(Collectors.joining(","));
                airPlan.setCycleExecutionTime(joinedString);
            }
            airPlan.setPlanStartTime(vo.getStartTime());
            airPlan.setPlanEndTime(vo.getEndTime());
            if (vo.getIntervalValues() != null) {
                airPlan.setIntervalValues(Arrays.toString(vo.getIntervalValues()).replaceAll("[\\[\\] ]", ""));
            }
            if (vo.getDayInterval() != null) {
                airPlan.setDayInterval(String.valueOf(vo.getDayInterval()));
            }
            //无人机编码
//            airPlan.setUavCode(vo.getUavCode());

            // 定义输入格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS);
            // 定义输出格式
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(DateUtils.YYYYMMDDHHMMSS);

            //比较执行时间 格式yyyy-MM-dd
            List<String> scheduleExecTimes = Lists.newArrayList();

            List<AirTaskReq> airTaskReqs = jsonArray.toJavaList(AirTaskReq.class);
            for (AirTaskReq airTaskReq : airTaskReqs) {
                if (StringUtils.isEmpty(airTaskReq.getRealityTime())) {
                    airTaskReq.setRealityTime(null);
                }
                if (StringUtils.isNotEmpty(airTaskReq.getScheduleExecTime())) {
                    // 解析字符串为 LocalDateTime 对象
                    LocalDateTime dateTime = LocalDateTime.parse(airTaskReq.getScheduleExecTime(), inputFormatter);
                    // 格式化为期望的输出格式
                    String formattedDateTime = dateTime.format(outputFormatter);
                    airTaskReq.setPlanName(airPlan.getPlanName() + "-" + formattedDateTime);
                }
                airTaskReq.setPlanId(planId);
                airTaskReq.setSnapshotId(airLine.getSnapshotId());
                airTaskReq.setFlightRouteId(airLine.getFlightRouteId());
                airTaskReq.setTenantId(tenantId);
                airTaskReq.setIndustryCode(industryCode);
                airTaskReq.setCreateUser(userName);
                if (Constants.ONE.equals(optType)) {
                    airTaskReq.setOriginalTaskRecordIds(vo.getOriginalTaskRecordIds());
                    airTaskReq.setTaskType(Constants.ONE);
                }
                airTaskReq.setAcceptNode(vo.getAcceptNode());
                airTaskReq.setStatus(airPlan.getStatus());
                airTaskReq.setAccessNode(vo.getAccessNode());
                airTaskReq.setStatus("0");
                airTaskReq.setMediumStatus("0");
                airTaskReq.setIsItEnabled("0");
                airTaskReq.setScheduleExecTime(airTaskReq.getScheduleExecTime());
                airTaskReq.setStartNestCode(airLine.getStartNestCode());
                airTaskReq.setEndNestCode(airLine.getEndNestCode());

                airPlan.setPlanTime(airTaskReq.getScheduleExecTime());
                if (!addOrEdit) {
                    airTaskReq.setUpdateUser(userName);
                    airTaskReq.setUpdateTime(new Date());
                }
                scheduleExecTimes.add(DateUtil.format(DateUtil.parse(airTaskReq.getScheduleExecTime(), DateUtils.YYYY_MM_DD_HH_MM_SS), DateUtils.YYYY_MM_DD));
            }
            //本地新增/编辑计划管理
            if (addOrEdit) {
                airPlan.setCreateUser(userName);
                airPlanMapper.addAirPlan(airPlan);
            } else {
                airPlan.setUpdateUser(userName);
                airPlanMapper.editAirPlan(airPlan);
                AirTaskReq airTaskReq = new AirTaskReq();
                airTaskReq.setUpdateUser(userName);
                airTaskReq.setPlanId(vo.getPlanId().toString());
                airTaskMapper.deleteByPlanId(airTaskReq);
            }
            // 处理安全员信息
            dutyAirPlanSafetyOfficer(addOrEdit, vo);
            airTaskMapper.insertBath(airTaskReqs);
            if (Constants.ONE.equals(optType)) {
                if (CollectionUtil.isNotEmpty(airTaskReqs)) {
                    String taskRecordId = airTaskReqs.get(0).getTaskRecordId();
                    //绑定本次复飞图片的id关系
                    String refId = vo.getPhotoId();
                    if (StringUtils.isNotEmpty(vo.getOriginalId())) {
                        refId = vo.getOriginalId();
                    }
                    insertReFlyRef(taskRecordId, refId, vo.getOriginalTaskRecordIds());
                }
            }
            return Result.data(airTaskReqs);
        } else {
            throw new CustomException("调用飞控平台新增计划返回数据为空");
        }
    }

    private void dutyAirPlanSafetyOfficer(boolean addOrEdit, AirPlanAddOrEditReq vo) {
        // 编辑：先清理旧数据；新增：无需清理
        if (!addOrEdit) {
            String userName = SecurityUtils.getLoginUser().getUsername();
            airPlanSafetyOfficerMapper.deleteByPlanId(vo.getPlanId().longValue(), userName);
        }

        if (CollectionUtil.isEmpty(vo.getSafetyOfficers())) {
            return;
        }

        String userName = SecurityUtils.getLoginUser().getUsername();
        List<AirPlanSafetyOfficer> list = vo.getSafetyOfficers().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    AirPlanSafetyOfficer data = new AirPlanSafetyOfficer();
                    data.setPlanId(vo.getPlanId().longValue());
                    data.setSafetyOfficer(item.getSafetyOfficer());
                    data.setSafetyOfficerMobile(item.getSafetyOfficerMobile());
                    data.setTenantId(vo.getTenantId());
                    data.setIndustryCode(vo.getIndustryCode());
                    data.setCreateUser(userName);
                    data.setCreateTime(new Date());
                    data.setDeleteFlag("0");
                    return data;
                })
                .collect(Collectors.toList());

        airPlanSafetyOfficerMapper.batchInsert(list);

        // 写入“用户记忆缓存”（安全员下拉历史），去重后仅保留最新 10 条
        try {
            cacheSafetyOfficerMemory(vo.getTenantId(), vo.getIndustryCode(), vo.getSafetyOfficers());
        } catch (Exception e) {
            log.error("安全员记忆缓存写入失败 planId={}", vo.getPlanId(), e);
        }


    }

    /**
     * 新增编辑计划的每天任务 删除校验
     *
     * @param
     */
    public void addPlanDelete(String planId, AirPlanAddOrEditReq vo, String platformCode) {
        //飞控不校验此规则，所以在这里需要删除新增的计划
        NetConnectUtils.RequestParamter<AirPlanAddOrEditReq> paramter1 = new NetConnectUtils.RequestParamter<>();
        //请求参数
        AirPlanAddOrEditReq deleteVo = new AirPlanAddOrEditReq();
        deleteVo.setPlanId(Integer.parseInt(planId));
        paramter1.setBody(deleteVo);
        NetConnectUtils.RequestParamter.Path path1 = new NetConnectUtils.RequestParamter.Path();
        //获取接口地址
        path1.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_DELETE_PLANID);
        path1.setPlanId(deleteVo.getPlanId());
        paramter1.setPath(path1);
        // 删除计划管理和任务
        try {
            log.info("新增调用飞控接口删除计划管理入参：{}", JSON.toJSONString(deleteVo));
            ResultEntity resultEntity1 = okhttpUtil.doPostUavGateWay(paramter1, vo.getAccessNode(), platformCode);
            log.info("新增调用飞控接口删除计划管理返回:{}", JSON.toJSONString(resultEntity1));
        } catch (Exception e) {
            log.error("新增调用飞控接口删除计划管理失败：", e);
        }
    }

    /**
     * 根据planId删除计划管理
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public Result deleteByPlanId(AirPlanAddOrEditReq vo) {
        AirPlanRep airPlanRep = airPlanMapper.findAirPlanById(Integer.valueOf(vo.getPlanId()));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //获取节点编码及登录用户名
        String platformCode = user.getPlatformCode();
        String userName = user.getUserName();
        String result = "";
        //调用飞控接口 删除计划管理
        if (ObjectUtil.isNotNull(vo)) {
            String joIn = String.valueOf(JSONObject.toJSON(vo));
            NetConnectUtils.RequestParamter<AirPlanAddOrEditReq> paramter = new NetConnectUtils.RequestParamter<>();
            //请求参数
            paramter.setBody(vo);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_DELETE_PLANID);
            path.setPlanId(vo.getPlanId());
            paramter.setPath(path);
            // 删除计划管理和任务
            try {
                log.info("调用飞控接口删除计划管理入参：" + joIn);
                ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, airPlanRep.getAccessNode(), platformCode);
                if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
                    result = JSON.parseObject(JSON.toJSONString(resultEntity.getData()), String.class);
                }
            } catch (Exception e) {
                log.error("调用飞控接口删除计划管理失败：{}", e);
            }
            //删除计划
            AirPlanRep airPlanById = airPlanMapper.findAirPlanById(Integer.valueOf(vo.getPlanId()));
            if (ObjectUtils.isNotEmpty(airPlanById)) {
                airPlanById.setUpdateUser(userName);
                airPlanMapper.deleteByPlanId(airPlanById);
                //删除计划下的任务（删除计划的时候直接删除，删除任务同步删除计划的时候不再删除任务）
                if (!Constants.ZERO.equals(vo.getTaskDeleteFlag())) {
                    AirTaskReq airTaskReq = new AirTaskReq();
                    airTaskReq.setUpdateUser(userName);
                    airTaskReq.setPlanId(airPlanById.getPlanId());
                    airTaskMapper.deleteByPlanId(airTaskReq);
                }
            }
        }
        return Result.data(result);
    }

    /**
     * 系统根据计划新增的数量自动生成一个名称“新建计划N”，可修改。
     *
     * @param tenantId
     * @param industryCode
     * @return
     */
    @Override
    public String getPlanName(String tenantId, String industryCode) {
        return airPlanMapper.getPlanName(tenantId, industryCode);
    }

    /**
     * 唯一性校验，重复时输入框高亮显示，同时在输入框下方显示提示“名称已存在！请重新输入。
     *
     * @param tenantId
     * @param industryCode
     * @param planName
     * @return
     */
    @Override
    public Result planNameIsRepeat(String tenantId, String industryCode, String planName, Integer id) {
        AirPlanRep airPlanRep = airPlanMapper.planNameIsRepeat(tenantId, industryCode, planName);
        if (ObjectUtils.isNotEmpty(airPlanRep)) {
            if ((id == null || !id.equals(airPlanRep.getId()))) {
                //说明计划名称重复
                return Result.fail(ResultCode.ERROR, "名称已存在！请重新输入。");
            }
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 巡检计划明细启用或关闭
     *
     * @param airTaskReq
     * @return
     */
    @Override
    @Transactional
    public Result updateIsItEnabled(AirTaskEnableReq airTaskReq) {
        if (ObjectUtil.isNull(airTaskReq)) {
            return Result.fail("请求为空");
        }
        List<AirTaskRes> airTaskRes = airTaskMapper.findListById(airTaskReq);
        //获取任务接入节点
        String accessNode = "";
        String acceptNode = "";
        if (CollectionUtil.isNotEmpty(airTaskRes)) {
            accessNode = airTaskRes.get(0).getAccessNode();
            acceptNode = airTaskRes.get(0).getAcceptNode();
        }
        String result = "";
        String platformCode = "";
        String userName = "";
        ResultEntity resultEntity = new ResultEntity();
        if (Constants.ONE.equals(airTaskReq.getIsSystem())) {
            userName = "system";
            platformCode = acceptNode;
        } else {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            //获取节点编码及登录用户名
            platformCode = user.getPlatformCode();
            userName = user.getUserName();
        }
        //是否启用
        String isItEnabled = airTaskReq.getIsItEnabled();
        String planId = airTaskReq.getPlanId();
        //启用类型 0计划 1任务
        String enabledType = airTaskReq.getEnabledType();
        //获取前端传入所有任务id
        List<String> taskIdList = airTaskReq.getEnableTaskList().stream().map(v -> v.getTaskRecordId()).collect(Collectors.toList());
        AirTaskEnableReq taskEnableReq = new AirTaskEnableReq();
        taskEnableReq.setTaskRecordIdList(taskIdList);
        taskEnableReq.setUpdateUser(userName);
        taskEnableReq.setIsItEnabled(isItEnabled);
        //如果启用状态为启用，否则启用状态为关闭
        if ("0".equals(isItEnabled)) {
            //调用飞控接口 巡检计划明细启用
            String joIn = String.valueOf(JSONObject.toJSON(taskEnableReq));
            NetConnectUtils.RequestParamter<AirTaskEnableReq> paramter = new NetConnectUtils.RequestParamter<>();
            //请求参数
            paramter.setBody(taskEnableReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_TASK_ON);
            paramter.setPath(path);
            try {
                log.info("调用飞控接口巡检计划明细启用入参：" + joIn);
                resultEntity = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
                log.info("调用飞控接口巡检计划明细启用结果：" + JSON.toJSONString(resultEntity));
                result = resultEntity.getErrorMsg();
            } catch (Exception e) {
                log.error("调用飞控接口巡检计划明细启用失败：{}", e);
            }
        } else {
            ///获取前端传入任务状态不为执行失败的所有任务id
            List<String> taskFilterIdList = airTaskReq.getEnableTaskList().stream().filter(v -> !"2".equals(v.getStatus())).map(v -> v.getTaskRecordId()).collect(Collectors.toList());
            AirTaskEnableReq taskFilterEnableReq = new AirTaskEnableReq();
            taskFilterEnableReq.setTaskRecordIdList(taskFilterIdList);
            taskFilterEnableReq.setUpdateUser(userName);
            //调用飞控接口 巡检计划明细关闭
            String joIn = String.valueOf(JSONObject.toJSON(taskFilterEnableReq));
            NetConnectUtils.RequestParamter<AirTaskEnableReq> paramter = new NetConnectUtils.RequestParamter<>();
            //请求参数
            paramter.setBody(taskFilterEnableReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAN_TASK_OFF);
            paramter.setPath(path);
            try {
                log.info("调用飞控接口巡检计划明细关闭入参：" + joIn);
                resultEntity = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
                log.info("调用飞控接口巡检计划明细关闭结果：" + JSON.toJSONString(resultEntity));
                result = resultEntity.getErrorMsg();
            } catch (Exception e) {
                log.error("调用飞控接口巡检计划明细关闭失败：{}", e);
            }
        }
        //如果飞控平台巡检计划明细启用成功，同步修改通用数据库
        if (ObjectUtil.isNotNull(resultEntity) && Constants.SUCCESS_CODE.equals(resultEntity.getReturnCode())) {
            result = resultEntity.getReturnMsg();
            if (!CollectionUtils.isEmpty(airTaskReq.getEnableTaskList())) {
                //更新 计划及任务表的启用状态
                airTaskMapper.updateEnabledTaskIdList(taskEnableReq);
            }
            boolean allEnabled = false;
            boolean allDisabled = false;
            if ("1".equals(enabledType)) {
                List<AirTaskReq> airTaskByPlanIdList = airTaskMapper.findAirTaskByPlanId(airTaskReq.getPlanId());
                // 判断该计划下的任务是否全部开启或关闭
                allEnabled = airTaskByPlanIdList.stream().allMatch(task -> "0".equals(task.getIsItEnabled()));
                allDisabled = airTaskByPlanIdList.stream().allMatch(task -> "1".equals(task.getIsItEnabled()));
            }
            //如果启用类型为计划，或者计划下的任务启用类型全部开启或关闭，更新计划启用状态
            if ("0".equals(enabledType) || allEnabled || allDisabled) {
                AirPlanRep planRep = new AirPlanRep();
                planRep.setPlanId(planId);
                planRep.setIsItEnabled(isItEnabled);
                planRep.setUpdateUser(userName);
                airPlanMapper.updateIsItEnabled(planRep);
            }

        }
        return Result.success(result);
    }

    @Override
    public Result querySafetyOfficerMemory(String safetyOfficer) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String userId = String.valueOf(user.getUserId());

        String key = buildUserMemoryKey(industryCode, tenantId, userId, USER_MEMORY_TYPE_SAFETY_OFFICER);
        String json = RedisUtil.get(key, RedisDBType.PRIMARY);
        if (StringUtils.isEmpty(json)) {
            return Result.data(Collections.emptyList());
        }
        List<AirPlanSafetyOfficerReq> list;
        try {
            list = JSON.parseArray(json, AirPlanSafetyOfficerReq.class);
        } catch (Exception e) {
            log.error("安全员记忆缓存解析失败 key={}", key, e);
            return Result.data(Collections.emptyList());
        }
        if (CollectionUtil.isEmpty(list)) {
            return Result.data(Collections.emptyList());
        }

        String kw = StringUtils.isEmpty(safetyOfficer) ? "" : safetyOfficer.trim();
        List<AirPlanSafetyOfficerReq> filtered = list.stream()
                .filter(Objects::nonNull)
                .filter(it -> StringUtils.isEmpty(kw) || (it.getSafetyOfficer() != null && it.getSafetyOfficer().contains(kw)))
                .limit(10)
                .collect(Collectors.toList());
        return Result.data(filtered);
    }

    private void cacheSafetyOfficerMemory(String tenantId, String industryCode, List<AirPlanSafetyOfficerReq> incoming) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(industryCode) || CollectionUtil.isEmpty(incoming)) {
            return;
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userId = String.valueOf(user.getUserId());
        String key = buildUserMemoryKey(industryCode, tenantId, userId, USER_MEMORY_TYPE_SAFETY_OFFICER);

        List<AirPlanSafetyOfficerReq> existing = Collections.emptyList();
        String oldJson = RedisUtil.get(key, RedisDBType.PRIMARY);
        if (!StringUtils.isEmpty(oldJson)) {
            try {
                existing = JSON.parseArray(oldJson, AirPlanSafetyOfficerReq.class);
            } catch (Exception ignore) {
                existing = Collections.emptyList();
            }
        }

        // 最新优先：先 incoming，再 existing
        List<AirPlanSafetyOfficerReq> merged = new ArrayList<>();
        incoming.stream().filter(Objects::nonNull).forEach(merged::add);
        if (CollectionUtil.isNotEmpty(existing)) {
            existing.stream().filter(Objects::nonNull).forEach(merged::add);
        }

        // 去重：按 safetyOfficer + mobile
        LinkedHashMap<String, AirPlanSafetyOfficerReq> dedup = new LinkedHashMap<>();
        for (AirPlanSafetyOfficerReq it : merged) {
            String name = it.getSafetyOfficer() == null ? "" : it.getSafetyOfficer().trim();
            String mobile = it.getSafetyOfficerMobile() == null ? "" : it.getSafetyOfficerMobile().trim();
            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(mobile)) {
                continue;
            }
            String dk = name + "|" + mobile;
            if (!dedup.containsKey(dk)) {
                AirPlanSafetyOfficerReq v = new AirPlanSafetyOfficerReq();
                v.setSafetyOfficer(name);
                v.setSafetyOfficerMobile(mobile);
                dedup.put(dk, v);
            }
            if (dedup.size() >= 10) {
                break;
            }
        }

        List<AirPlanSafetyOfficerReq> latest10 = new ArrayList<>(dedup.values());
        RedisUtil.set(key, JSON.toJSONString(latest10), RedisDBType.PRIMARY);
    }

    private String buildUserMemoryKey(String industryCode, String tenantId, String userId, String type) {
        return REDIS_USER_MEMORY_PREFIX + industryCode + ":" + tenantId + ":" + userId + ":" + type;
    }

    @Override
    public Result<List<String>> selectSafetyOfficerPhonesByFlyingUav(String uavCode) {
        List<String> phones = airPlanSafetyOfficerMapper.selectSafetyOfficerPhonesByFlyingUav(uavCode);
        return Result.data(phones);
    }

    @Override
    public Result<String> selectUavNameByUavCode(String uavCode) {
        return Result.data(airPlanSafetyOfficerMapper.selectUavNameByUavCode(uavCode));
    }

    /**
     * 计划名称自动填充为“复制的计划名称_复制N”
     *
     * @param planId
     * @param tenantId
     * @param industryCode
     * @return
     */
    @Override
    public String copyPlanName(String planId, String tenantId, String industryCode) {
        String planName = airPlanMapper.findPlanNameByPlanId(planId);

        Integer count = airPlanMapper.copyPlanName(planName, tenantId, industryCode);
        if (count == null) {
            return planName + "_复制01";
        }
        if (count >= 1 && count <= 9) {
            return planName + "_复制" + 0 + count;
        }
        return planName + "_复制" + count;
    }

    /**
     * 修改任务类型
     *
     * @param originalTaskRecordIds
     */
    @Override
    public void editTaskType(String originalTaskRecordIds) {
        airTaskMapper.editTaskType(originalTaskRecordIds);
    }

    /**
     *
     * @param originalTaskRecordIds
     * @param photoId
     */
    public void insertReFlyRef(String originalTaskRecordIds, String photoId, String originalTaskRecordId) {
        airTaskMapper.insertReFlyRef(originalTaskRecordIds, photoId, originalTaskRecordId);
    }
}
