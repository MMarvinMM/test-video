package com.chinatower.sync.consumer.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.api.IAirMediumService;
import com.chinatower.device.api.IAirTaskService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.sync.consumer.dao.SyncAlgorithmsDto;
import com.chinatower.sync.consumer.dao.UavReceiveMapper;
import com.chinatower.sync.consumer.entity.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WLF
 * @description
 * @date 2024/11/10 16:24
 */
@Slf4j
@Component
public class UavReceiveService {
    @Resource
    UavReceiveMapper uavReceiveMapper;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    private MsgFeignClient msgFeignClient;
    @Resource
    private IAirTaskService airTaskService;
    @Resource
    private IAirMediumService airMediumService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private SystemFeignClient systemFeignClient;

    @Value("${algorithm.ak}")
    private String algAk;
    @Value("${algorithm.sk}")
    private String algSk;
    @Value("${algorithm.appCode}")
    private String algAppCode;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @param uavStatusRequest
     */
    public void receiveAirLineTaskStatus(UavStatusRequest uavStatusRequest) {
        log.info("无人机航线计划任务状态变更:{}", uavStatusRequest);

        AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.GUIDE_FLIGHT_MOUNT_SWITCH);
        String kaiguan = Constants.ZERO;
        if (String.valueOf(Constants.CODE_200).equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            kaiguan = sysConfig.getConfigValue();
        }
        log.info("无人机航线计划任务状态变更开关 :{}",kaiguan);
        if (Constants.ONE.equals(kaiguan)) {
            //返航或者到达航点喇叭相关
            if (Constants.FIVE.equals(uavStatusRequest.getExecStatus())
                    || Constants.ONE.equals(uavStatusRequest.getWaypointState())) {
                CompletableFuture.runAsync(() -> {
                    try {
                        String cacheKey = Constants.TASK_STATUS_PROCESS_KEY + uavStatusRequest.getTaskRecordId() + uavStatusRequest.getWaypointIndex();
                        boolean lock = RedisUtil.setIfAbsent(cacheKey, Constants.LOCK, 300, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                        if (lock) {
                            AirTaskStatusProcessDto dto = new AirTaskStatusProcessDto();
                            BeanUtils.copyProperties(uavStatusRequest, dto);

                            log.info("【无人机喇叭任务】开始异步执行，设备编码：{}，航点状态：{}",
                                    dto.getDeviceCode(), dto.getWaypointState());

                            // 执行喇叭任务
                            airMediumService.taskStatusProcess(dto);

                            log.info("【无人机喇叭任务】异步执行完成，设备编码：{}", dto.getDeviceCode());
                        }
                    } catch (Exception e) {
                        log.error("【无人机喇叭任务】返航/到达航点喇叭相关处理失败，设备编码：{}",
                                uavStatusRequest.getDeviceCode(), e);
                    }
                }, threadPoolTaskExecutor);
            }
        }
        //执行失败，且有断点信息，业务状态设置为航线断点
        if (Constants.TWO.equals((uavStatusRequest.getExecStatus())) && ObjectUtil.isNotNull(uavStatusRequest.getBreakPointReqJson())) {
            uavStatusRequest.setExecStatus(Constants.FOUR);
        }
        if (Constants.ONE.equals(uavStatusRequest.getExecStatus())) {
            //查询本条任务 和是否为复飞任务 如果为复飞任务 需要同步修改复飞状态
            AirUavStatus airUavStatus = uavReceiveMapper.queryTaskById(uavStatusRequest.getTaskRecordId());
            if (ObjectUtil.isNotEmpty(airUavStatus)) {
                String originalTaskRecordId = airUavStatus.getOriginalTaskRecordId();
                //修改原任务的类型为 已复飞任务
                uavReceiveMapper.editTaskType(originalTaskRecordId);
            }
            //任务完成时候 根据航点策略生成航点拍照任务
            try {
                insertAirLinePhotoTask(uavStatusRequest.getTaskRecordId());
            } catch (Exception e) {
                log.info("任务{}生成航点拍照任务失败", uavStatusRequest.getTaskRecordId());
            }
            //航线自动二次识别任务
            try {
                insertAirLinePointTask(uavStatusRequest.getTaskRecordId());
            } catch (Exception e) {
                log.info("任务{}生成航点自动二次识别任务失败", uavStatusRequest.getTaskRecordId());
            }
            // 更新实际里程
            try {
                log.info("更新实际里程:任务id：{}", uavStatusRequest.getTaskRecordId());
                String redisKey = CacheConstants.UAV_ACCUMULATE_MILEAGE + uavStatusRequest.getTaskRecordId();
                String cacheData = RedisUtil.get(redisKey, RedisDBType.PRIMARY);
                log.info("任务id:{},缓存数据：{}", uavStatusRequest.getTaskRecordId(), cacheData);
                if (ObjectUtil.isNotEmpty(cacheData)) {
                    JSONObject cacheDataJson = JSON.parseObject(cacheData);
                    Double accumulateMileage = cacheDataJson.getDouble("accumulateMileage");
                    AirTaskReq airTaskReq = new AirTaskReq();
                    airTaskReq.setTaskRecordId(uavStatusRequest.getTaskRecordId());
                    airTaskReq.setAccumulateMileage(String.valueOf(accumulateMileage));
                    airTaskService.updateTaskAccumulateMileage(airTaskReq);
                    log.info("任务id:{},更新实际里程成功,里程：{}", uavStatusRequest.getTaskRecordId(), accumulateMileage);
                }
            } catch (Exception e) {
                log.info("更新实际里程失败", e);
            }
        }

        //更新计划任务状态(execStatus = 5 返航中不更新状态)
        uavReceiveMapper.receiveAirLineTaskStatus(uavStatusRequest);

        if (Constants.ONE.equals(uavStatusRequest.getExecStatus()) || Constants.TWO.equals(uavStatusRequest.getExecStatus()) || Constants.FOUR.equals(uavStatusRequest.getExecStatus())) {
            //任务执行成功或者执行失败均代表该航线该任务执行完毕，此时通过任务id查询该航线下是否有临时算法
            //根据任务id查询所属航线
            String floutId = uavReceiveMapper.queryAirLineByTaskId(uavStatusRequest.getTaskRecordId());
            List<TempAlgorithmResp> twoList = uavReceiveMapper.selectTempAlgorithm(floutId);
            if (!CollectionUtils.isEmpty(twoList)) {
                log.info("receiveAirLineTaskStatus-任务{}临时算法数据:{}", uavStatusRequest.getTaskRecordId(), JSON.toJSONString(twoList));
                if (!CollectionUtils.isEmpty(twoList)) {
                    //该航线下有临时算法，此时需将临时算法关闭，并将此批数据做假删
                    for (TempAlgorithmResp resp : twoList) {
                        SyncAlgorithmsDto dto = new SyncAlgorithmsDto();
                        BeanUtils.copyProperties(resp, dto);
                        dto.setAlgorithmType(Constants.TWO);
                        dto.setOptType(Constants.THREE);
                        syncAlgorithm(dto, resp);
                    }
                }
                //假删无人机算法下发表数据 的临时算法
                uavReceiveMapper.delAirAlgorithm(floutId);
            }
            //查询该任务对应航线需要恢复的算法，冲新下发算法仓，并将此批数据做恢复
            List<TempAlgorithmResp> oneList = uavReceiveMapper.selectOneAlgorithm(floutId);
            if (!CollectionUtils.isEmpty(oneList)) {
                log.info("receiveAirLineTaskStatus-任务{}待恢复算法数据:{}", uavStatusRequest.getTaskRecordId(), JSON.toJSONString(oneList));
                if (!CollectionUtils.isEmpty(twoList)) {
                    for (TempAlgorithmResp resp : oneList) {
                        SyncAlgorithmsDto dto = new SyncAlgorithmsDto();
                        BeanUtils.copyProperties(resp, dto);
                        dto.setAlgorithmType(Constants.ONE);
                        dto.setOptType(Constants.TWO);
                    }
                }
                //恢复无人机算法下发表数据 的正常算法
                uavReceiveMapper.updateAirAlgorithmNormal(floutId);
            }
        }

    }

    /**
     * 新增航点自动二次识别任务
     *
     * @param taskRecordId
     */
    @Async
    private void insertAirLinePointTask(String taskRecordId) {
        log.info("insertAirLinePointTask-任务id:{}", taskRecordId);
        List<AirLinePointTaskReq> airLinePointTaskReq = uavReceiveMapper.queryPointInfo(taskRecordId);
        //插入航点自动二次识别任务
        if (ObjectUtil.isNull(airLinePointTaskReq)) {
            return;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("taskRecordId").is(taskRecordId));
        List<AirLinePointTaskReq> airLinePointTaskRevs = mongoTemplate.find(query, AirLinePointTaskReq.class, com.chinatower.event.constants.Constants.UAV_AIR_POINT_TASK);
        if (CollectionUtil.isNotEmpty(airLinePointTaskRevs)) {
            return;
        }
        airLinePointTaskReq.forEach(airLinePointTaskReq1 -> {
            airLinePointTaskReq1.setStatus(0);
            LocalDateTime halfHourLater = LocalDateTime.now().plusMinutes(30);
            Date executeTime = Date.from(halfHourLater.atZone(ZoneId.systemDefault()).toInstant());
            airLinePointTaskReq1.setExecuteTime(executeTime);
        });
        log.info("insertAirLinePointTask-航点自动二次识别任务,{}", JSONObject.toJSON(airLinePointTaskReq));
        if (CollectionUtil.isEmpty(airLinePointTaskReq)) {
            return;
        }
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_AIR_POINT_TASK);
        bulkOperations.insert(airLinePointTaskReq);
        bulkOperations.execute();
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void insertAirLinePhotoTask(String taskRecordId) {
        log.info("insertAirLinePhotoTask-任务id:{}", taskRecordId);
        List<PointsCompareTask> pointsCompareTasks = uavReceiveMapper.queryPhotoTaskInfo(taskRecordId);
        if (CollectionUtil.isEmpty(pointsCompareTasks)) {
            return;
        }
        log.info("insertAirLinePhotoTask-查询到策略:{}", JSONObject.toJSONString(pointsCompareTasks));
        List<AirLinePhotoTask> airLinePhotoTasks = new ArrayList<>();
        pointsCompareTasks.forEach(pointsCompareTask -> {
            LocalDate today = LocalDate.now();
            pointsCompareTask.setTaskRecordId(taskRecordId);
            // 1. 检查是否在时间范围内
            LocalDate startDate = LocalDate.parse(pointsCompareTask.getExecutionStartDate(), DATE_TIME_FORMATTER);
            LocalDate endDate = LocalDate.parse(pointsCompareTask.getExecutionEndDate(), DATE_TIME_FORMATTER);
            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                return;
            }
            // 判断此航线今天是否已经执行过策略
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            String formattedDate = currentDate.format(formatter);
            List<String> ids = uavReceiveMapper.queryTaskByAirLineId(pointsCompareTask.getFlightRouteId(), formattedDate);
            if (CollectionUtil.isNotEmpty(ids)) {
                log.info("insertAirLinePhotoTask-任务{}今天已执行过该策略,跳过", taskRecordId);
            }
            // 2. 根据策略类型检查特定条件
            switch (pointsCompareTask.getCompareStrategy()) {
                case "1": // 周策略
                    DayOfWeek targetDay = mapChineseDayToDayOfWeek(pointsCompareTask.getCompareDate());
                    if (today.getDayOfWeek() == targetDay) {
                        LocalDate lastCycleDate = today.minusWeeks(1); // 上周同一天
                        // 检查对比日期是否在任务时间范围内
                        log.info("insertAirLinePhotoTask-周策略");
                        if (isDateInRange(lastCycleDate, startDate, endDate)) {
                            log.info("insertAirLinePhotoTask-周策略满足时间");
                            airLinePhotoTasks.add(createPhotoTask(pointsCompareTask, taskRecordId, lastCycleDate));
                        }
                    }
                    break;
                case "2": // 月策略
                    int dayOfMonth = Integer.parseInt(pointsCompareTask.getCompareDate());
                    if (today.getDayOfMonth() == dayOfMonth) {
                        LocalDate lastCycleDate = today.minusMonths(1); // 上月同一天
                        // 检查对比日期是否在任务时间范围内
                        if (isDateInRange(lastCycleDate, startDate, endDate)) {
                            airLinePhotoTasks.add(createPhotoTask(pointsCompareTask, taskRecordId, lastCycleDate));
                        }
                    }
                    break;
                case "3": // 年策略
                    DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("MM-dd");
                    String currentMonthDay = today.format(monthDayFormatter);
                    if (currentMonthDay.equals(pointsCompareTask.getCompareDate())) {
                        LocalDate lastCycleDate = today.minusYears(1); // 去年同一天
                        // 检查对比日期是否在任务时间范围内
                        if (isDateInRange(lastCycleDate, startDate, endDate)) {
                            airLinePhotoTasks.add(createPhotoTask(pointsCompareTask, taskRecordId, lastCycleDate));
                        }
                    }
                    break;
                case "4": // 自定义间隔天数
                    int intervalDays = Integer.parseInt(pointsCompareTask.getCompareDate());
                    LocalDate lastCycleDate = today.minusDays(intervalDays); // 对比日期 = 今天 - intervalDays
                    // 检查对比日期是否在任务时间范围内（保持原有逻辑，与其他策略统一）
                    if (isDateInRange(lastCycleDate, startDate, endDate)) {
                        airLinePhotoTasks.add(createPhotoTask(pointsCompareTask, taskRecordId, lastCycleDate));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("无效的策略类型: " + pointsCompareTask.getTaskRecordId());
            }
        });
        //插入拍照定时任务
        log.info("insertAirLinePhotoTask-插入定时任务,{}", JSONObject.toJSON(airLinePhotoTasks));
        uavReceiveMapper.insertAirLinePhotoTask(airLinePhotoTasks);
    }

    // 抽取通用的日期范围检查方法
    private boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }


    // 创建拍照任务的公共方法
    private AirLinePhotoTask createPhotoTask(PointsCompareTask pointsCompareTask, String taskRecordId, LocalDate lastCycleDate) {
        AirLinePhotoTask airLinePhotoTask = new AirLinePhotoTask();
        airLinePhotoTask.setCompareId(pointsCompareTask.getId());
        airLinePhotoTask.setCompareStatus(Constants.ZERO);
        airLinePhotoTask.setCompareTaskRecordId(taskRecordId);
        // 设置定时任务执行时间(半小时后)
        LocalDateTime nowPlus30Minutes = LocalDateTime.now().plusMinutes(30);
        airLinePhotoTask.setCompareDate(nowPlus30Minutes.format(DATE_TIME_FORMATTER));

        // 设置上个周期日期
        airLinePhotoTask.setLastCycleDate(lastCycleDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        pointsCompareTask.setLastCycleDate(lastCycleDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        // 查询上个时间的任务id存储
        if (airLinePhotoTask.getLastCycleDate() != null) {
            log.info("insertAirLinePhotoTask-查询上个周期时间,{}", JSONObject.toJSON(airLinePhotoTask.getLastCycleDate()));
            AirUavStatus airUavStatus = uavReceiveMapper.queryTask(pointsCompareTask);
            log.info("insertAirLinePhotoTask-查询上个周期任务,{}", JSONObject.toJSON(airUavStatus));
            if (ObjectUtil.isNotEmpty(airUavStatus)) {
                airLinePhotoTask.setTaskRecordId(airUavStatus.getTaskRecordId());
            } else {
                airLinePhotoTask = null;
            }
        } else {
            airLinePhotoTask = null;
        }
        return airLinePhotoTask;
    }

    // 中文星期转换为DayOfWeek枚举
    private static DayOfWeek mapChineseDayToDayOfWeek(String chineseDay) {
        switch (chineseDay) {
            case "1":
                return DayOfWeek.MONDAY;
            case "2":
                return DayOfWeek.TUESDAY;
            case "3":
                return DayOfWeek.WEDNESDAY;
            case "4":
                return DayOfWeek.THURSDAY;
            case "5":
                return DayOfWeek.FRIDAY;
            case "6":
                return DayOfWeek.SATURDAY;
            case "7":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("无效的星期参数: " + chineseDay);
        }
    }


    public void syncAlgorithm(SyncAlgorithmsDto dto, TempAlgorithmResp resp) {
        dto.setAlgorithmCode(resp.getWarningTypeId());
        if (Constants.THREE.equals(dto.getOptType())) {
            dto.setTaskId(resp.getTaskId());
        } else {
            dto.setTaskId(IdUtil.simpleUUID());
        }
        //3航线 待定
        dto.setAnalyType("3");
        dto.setArriveIdx("");
        dto.setAreaId("");
        dto.setChannelCode(resp.getDeviceCode());
        dto.setTaskVersion(1l);
        NetConnectUtils.RequestParamter<SyncAlgorithmsDto> paramterAl = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path pathAl = new NetConnectUtils.RequestParamter.Path();
        pathAl.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_ALGORITHMS_TASK);
        paramterAl.setPath(pathAl);
        paramterAl.setBody(dto);
        ResultEntity result = doPostAlgGateWay(paramterAl, dto.getAccessNode(), dto.getAcceptNode());

        if (ObjectUtil.isNotEmpty(result) && "0000".equals(result.getReturnCode())) {
            if (Constants.ONE.equals(dto.getOptType())) {
                dto.setStatus(Constants.ZERO);
            } else {
                dto.setStatus(Constants.ONE);
            }
        }
        log.info("新增编辑航线实时流任务{}下发结果{}", dto.getFlightRouteId(), JSONObject.toJSON(result));
        //调用信息存入mongo
        AirAlTaskLogReq airAlTaskLogReq = new AirAlTaskLogReq();
        airAlTaskLogReq.setTaskId(dto.getTaskId());
        airAlTaskLogReq.setTaskJson(JSONObject.toJSONString(dto));
        airAlTaskLogReq.setReturnMsg(result.getReturnMsg());
        airAlTaskLogReq.setReturnCode(result.getReturnCode());
        airAlTaskLogReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        airAlTaskLogReq.setErrMsg(result.getErrorMsg());
        airAlTaskLogReq.setAccessNode(dto.getAccessNode());
        airAlTaskLogReq.setAcceptNode(dto.getAcceptNode());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.AIR_TASK_AL_SYNC_LOG);
        bulkOperations.insert(airAlTaskLogReq);
        bulkOperations.execute();
    }


    /**
     * 算法仓运管调用方式
     *
     * @param
     * @param
     * @return
     */
    public ResultEntity doPostAlgGateWay(NetConnectUtils.RequestParamter requestParamter, String toPlatformCode, String platformCode) {
        ResultEntity cr = new ResultEntity();
        try {
            log.info("开始调用新大陆网关:接入节点{}接收节点{}", toPlatformCode, platformCode);
            cr = connectUtils.post(requestParamter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_AppCode.of(algAppCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("algorithm"),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(requestParamter)));
            log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关:{}", e);
        }
        return cr;
    }

    /**
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void editAirTaskStatus(UavAlgorithmRequest request) {
        log.info("无人机算法任务状态同步:{}", request);
        uavReceiveMapper.editAirTaskStatus(request);
    }

    /**
     *
     * @param uavAlgorithmRequest
     */
    public void insertUavUfcpInfo(UavFlyReq uavAlgorithmRequest) {
        log.info("无人机遥测数据同步:{}", uavAlgorithmRequest);
        uavAlgorithmRequest.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_SYNC_UFNC);
        bulkOperations.insert(uavAlgorithmRequest);
        bulkOperations.execute();
    }

    /**
     * 无人机设备状态上报
     *
     * @param airUavStatus
     */
    public void uavStatus(AirUavStatus airUavStatus) {
        log.info("无人机设备状态上报:{}", airUavStatus);
        airUavStatus.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_SYNC_STATUS);
        bulkOperations.insert(airUavStatus);
        bulkOperations.execute();
    }

    /**
     * 查询无人机信息
     *
     * @param deviceCode
     */
    public AirUavStatus queryUavByDeviceCode(String deviceCode) {
        AirUavStatus uav = uavReceiveMapper.queryUavByDeviceCode(deviceCode);
        return uav;
    }

    /**
     * 无人机报警策略
     *
     * @param deviceCodes
     */
    public List<UavAlarmStrategyVO> selectUavAlarmStrategyInfo(List<String> deviceCodes) {
        List<UavAlarmStrategyVO> uavAlarmStrategyVOS = uavReceiveMapper.selectUavAlarmStrategyInfo(deviceCodes);
        return uavAlarmStrategyVOS;
    }

    /**
     * 短信发送
     *
     * @param realTimeList
     */
    public void sendSms(List<UavAlarmStrategyVO> realTimeList, AIrUavConnectionInfo aIrUavConnectionInfo, String smsCode, String optType) throws ParseException {
        if (CollectionUtils.isEmpty(realTimeList)) {
            return;
        }
        log.info("无人机报警实时发送信息:{},无人机信息:{}", JSONObject.toJSON(realTimeList), JSONObject.toJSON(aIrUavConnectionInfo));
        Date date = new Date(aIrUavConnectionInfo.getTimestamp());
        SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.DATE_YMDHMS_STR);
        String targetDateStr = outputFormat.format(date);
        //分租户批次发送短信
        List<String> userList = new ArrayList<>();
        List<UavAlarmInfo> uavAlarmInfoList = new ArrayList<>();
        realTimeList.stream().forEach(uavAlarmStrategyVO -> {
            String userIds = uavAlarmStrategyVO.getAlarmUserId();
            List<String> u = Arrays.asList(userIds.split(","));
            userList.addAll(u);
            //无人机失联告警信息
            String sendTimeStr = com.chinatower.common.core.utils.DateUtils.getTime();
            String type = Constants.ZERO;
            String delFlag = Constants.ONE;
            aIrUavConnectionInfo.setStatus(Constants.ONE);
            UavAlarmInfo uavAlarmInfo = getUavAlarmInfo(aIrUavConnectionInfo, uavAlarmStrategyVO, sendTimeStr, type, delFlag);
            uavAlarmInfoList.add(uavAlarmInfo);
        });
        String userIds = realTimeList.stream()
                .map(UavAlarmStrategyVO::getAlarmUserId)
                .collect(Collectors.joining(","));
        List<UserInfoDto> userInfoDtoList = uavReceiveMapper.queryUserInfo(Arrays.asList(userIds.split(",")));
        String phoneNumbersStr = userInfoDtoList.stream()
                .map(UserInfoDto::getPhonenumber)
                .collect(Collectors.joining(","));
        Map<String, Object> map = new HashMap<>();
        map.put("deviceName", aIrUavConnectionInfo.getUavName());
        map.put("time", targetDateStr);
        map.put("address", aIrUavConnectionInfo.getAddress());
        map.put("longitude", aIrUavConnectionInfo.getLongitude());
        map.put("latitude", aIrUavConnectionInfo.getLatitude());
        CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
        commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
        commonSmsRequest.setPhone(phoneNumbersStr);
        commonSmsRequest.setRequestMap(JSON.toJSONString(map));
        commonSmsRequest.setTemplateCode(smsCode);
        try {
            msgFeignClient.sendMessage(commonSmsRequest);
        } catch (Exception e) {
            log.error("sms error");
        }
        if (Constants.ONE.equals(optType)) {
            uavReceiveMapper.insertUavAlarmInfo(uavAlarmInfoList);
        }
    }

    /**
     * 无人机延迟报警信息
     *
     * @param delayList
     */
    public void insertUavAlarmInfo(List<UavAlarmStrategyVO> delayList, AIrUavConnectionInfo aIrUavConnectionInfo) {
        if (CollectionUtils.isEmpty(delayList)) {
            return;
        }
        log.info("无人机失联报警延迟发送信息:{},无人机失联信息:{}", JSONObject.toJSON(delayList), JSONObject.toJSON(aIrUavConnectionInfo));
        List<UavAlarmInfo> uavAlarmInfoList = new ArrayList<>();
        delayList.stream().forEach(uavAlarmStrategyVO -> {
            //计算延迟时间
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_YMDHMS);
            String alarmDelayHour = Optional.ofNullable(uavAlarmStrategyVO.getAlarmDelayHour())
                    .filter(s -> !s.isEmpty())
                    .orElse(Constants.ZERO);
            String alarmDelayMinute = Optional.ofNullable(uavAlarmStrategyVO.getAlarmDelayMinute())
                    .filter(s -> !s.isEmpty())
                    .orElse(Constants.ZERO);
            Date unConnectDate = new Date(aIrUavConnectionInfo.getTimestamp());
            int totalDelayMinutes = Integer.parseInt(alarmDelayHour) * 60 + Integer.parseInt(alarmDelayMinute);
            Date sendDate = new Date(unConnectDate.getTime() + (long) totalDelayMinutes * 60 * 1000);
            String sendTimeStr = sdf.format(sendDate);
            String type = Constants.ONE;
            String delFlag = Constants.ZERO;
            aIrUavConnectionInfo.setStatus(Constants.ZERO);
            UavAlarmInfo uavAlarmInfo = getUavAlarmInfo(aIrUavConnectionInfo, uavAlarmStrategyVO, sendTimeStr, type, delFlag);
            uavAlarmInfoList.add(uavAlarmInfo);
        });
        uavReceiveMapper.insertUavAlarmInfo(uavAlarmInfoList);
    }

    /**
     * 无人机延迟报警信息
     *
     * @param aIrUavConnectionInfo
     * @param uavAlarmStrategyVO
     * @param sendTimeStr
     * @return
     */
    private static UavAlarmInfo getUavAlarmInfo(AIrUavConnectionInfo aIrUavConnectionInfo, UavAlarmStrategyVO uavAlarmStrategyVO, String sendTimeStr, String type, String delFlag) {
        UavAlarmInfo uavAlarmInfo = new UavAlarmInfo();
        uavAlarmInfo.setDeviceCode(aIrUavConnectionInfo.getDeviceCode());
        uavAlarmInfo.setUavName(aIrUavConnectionInfo.getUavName());
        uavAlarmInfo.setLatitude(aIrUavConnectionInfo.getLatitude());
        uavAlarmInfo.setLongitude(aIrUavConnectionInfo.getLongitude());
        uavAlarmInfo.setType(type);
        uavAlarmInfo.setSendUserId(uavAlarmStrategyVO.getAlarmUserId());
        uavAlarmInfo.setUnConnectTime(aIrUavConnectionInfo.getTimestampStr());
        uavAlarmInfo.setSendTime(sendTimeStr);
        uavAlarmInfo.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        uavAlarmInfo.setUavCode(aIrUavConnectionInfo.getUavCode());
        uavAlarmInfo.setAddress(aIrUavConnectionInfo.getAddress());
        uavAlarmInfo.setStatus(aIrUavConnectionInfo.getStatus());
        uavAlarmInfo.setTenantId(uavAlarmStrategyVO.getTenantId());
        uavAlarmInfo.setIndustryCode(uavAlarmStrategyVO.getIndustryCode());
        uavAlarmInfo.setDelFlag(delFlag);
        return uavAlarmInfo;
    }

    /**
     * 删除无人机报警信息
     *
     * @param deviceCode
     */
    public int deleteUavAlarmInfo(String deviceCode) {
        int i = uavReceiveMapper.deleteUavAlarmInfo(deviceCode);
        return i;
    }

    /**
     * 查询无人机报警信息
     *
     * @param deviceCode
     */
    public List<UavAlarmInfo> queryUavAlarmInfo(String deviceCode) {
        List<UavAlarmInfo> uavAlarmInfo = uavReceiveMapper.queryUavAlarmInfo(deviceCode);
        return uavAlarmInfo;
    }

    public AirUavStatus queryUavByUavCode(String deviceCode) {
        return uavReceiveMapper.queryUavByUavCode(deviceCode);
    }
}
