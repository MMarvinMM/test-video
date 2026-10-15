package com.chinatower.platform.modules.alarm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.IAlarmOperateTaskService;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AgentOperationTaskReminderDO;
import com.chinatower.event.entity.dto.AiJudgeResultDTO;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.AiJudgeAlarmVO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.message.api.OperationJudgeMsgAPI;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.WebSocketMsgType;
import com.chinatower.platform.common.OverdueNotifyConfig;
import com.chinatower.platform.modules.alarm.dao.AgentOperationTaskExecutionDAO;
import com.chinatower.platform.modules.alarm.dao.AgentOperationTaskReminderDAO;
import com.chinatower.platform.modules.alarm.dao.AlgorithmWeightDAO;
import com.chinatower.platform.modules.alarm.entity.VmAlgorithmWeightDO;
import com.chinatower.platform.modules.alarm.entity.VmJudgeTaskExecutionDO;
import com.chinatower.platform.modules.alarm.service.OperationAiJudgeNotificationService;
import com.chinatower.platform.utils.BigDataPlatformHttpUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OperationAiJudgeNotificationServiceImpl implements OperationAiJudgeNotificationService {

    @Autowired
    private OperationJudgeMsgAPI operationJudgeMsgAPI;

    @Autowired
    private IAlarmOperateTaskService iAlarmOperateTaskService;

    @Autowired
    private ITowerEventWarningService iTowerEventWarningService;

    @Autowired
    private AgentOperationTaskExecutionDAO vmAgentOperationTaskExecutionDAO;

    @Autowired
    private AgentOperationTaskReminderDAO agentOperationTaskReminderDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private BigDataPlatformHttpUtil bigDataPlatformHttpUtil;

    @Autowired
    private AlgorithmWeightDAO algorithmWeightDAO;

    @Override
    public void automaticJudgeResult(String param) {
        log.info("automaticJudgeResult 自动研判结果通知开始执行，入参 param={}", param);
        try {
            // 1、查询开启的研判任务
            List<OperationTaskVO> taskList = queryOpenJudgeTasks();
            if (CollectionUtils.isEmpty(taskList)) {
                log.info("automaticJudgeResult 未查询到开启自动研判通知用户任务");
                return;
            }

            // 2、处理每个任务
            processJudgeTasks(taskList);
        } catch (Exception e) {
            log.error("automaticJudgeResult 自动研判结果通知执行异常", e);
        } finally {
            log.info("automaticJudgeResult 自动研判结果通知执行结束");
        }
    }

    /**
     * 查询开启的研判任务
     *
     * @return 任务列表
     */
    private List<OperationTaskVO> queryOpenJudgeTasks() {
        AlarmOperateTaskDTO taskRequest = new AlarmOperateTaskDTO();
        Map<String, Object> taskStatus = new HashMap<>();
        taskStatus.put("taskStatus", Constants.AI_JUDGE_TASK_STATUS_OPEN);
        taskRequest.setInputs(taskStatus);
        taskRequest.setSingleLabel("999999");

        Result<List<OperationTaskVO>> taskResult = iAlarmOperateTaskService.getTaskList(taskRequest);
        if (taskResult == null || taskResult.getCode() != ResultCode.SUCCESS.getCode()) {
            log.warn("automaticJudgeResult 查询开启的研判任务失败，taskResult：{}",
                    taskResult == null ? "null" : JSON.toJSONString(taskResult));
            return Collections.emptyList();
        }

        List<OperationTaskVO> data = taskResult.getData();
        log.info("automaticJudgeResult 查询到开启的研判任务数量：{}，任务详情：{}",
                CollectionUtils.isEmpty(data) ? 0 : data.size(), JSONObject.toJSONString(data));
        return CollectionUtils.isEmpty(data) ? Collections.emptyList() : data;
    }

    /**
     * 处理研判任务列表
     *
     * @param taskList 任务列表
     */
    private void processJudgeTasks(List<OperationTaskVO> taskList) {
        // 提取用户ID列表
        List<String> userIdList = taskList.stream()
                .map(OperationTaskVO::getUserId)
                .distinct()
                .collect(Collectors.toList());
        log.info("automaticJudgeResult 开启自动研判的用户数量：{}，用户列表：{}",
                userIdList.size(), JSONObject.toJSONString(userIdList));

        // 查询执行中的记录
        List<VmJudgeTaskExecutionDO> executionList = vmAgentOperationTaskExecutionDAO.selectByUserIds(
                userIdList, Constants.ONE_NUM, Constants.AUTO_JUDGE_TASK_EXECUTION_TYPE);
        log.info("automaticJudgeResult 查询到执行中的研判任务记录数量：{}",
                CollectionUtils.isEmpty(executionList) ? 0 : executionList.size());

        // 构建任务ID到执行记录的映射
        Map<Long, VmJudgeTaskExecutionDO> executionMap = executionList.stream()
                .collect(Collectors.toMap(VmJudgeTaskExecutionDO::getTaskId, execution -> execution, (k1, k2) -> k1));

        // 处理每个任务
        for (OperationTaskVO task : taskList) {
            processSingleJudgeTask(task, executionMap);
        }
    }

    /**
     * 处理单个研判任务
     *
     * @param task         任务信息
     * @param executionMap 执行记录映射
     */
    private void processSingleJudgeTask(OperationTaskVO task, Map<Long, VmJudgeTaskExecutionDO> executionMap) {
        try {
            log.info("automaticJudgeResult 开始处理任务，用户ID：{}，任务ID：{}，任务信息：{}",
                    task.getUserId(), task.getTaskId(), JSON.toJSONString(task));

            // 1. 计算周期时间
            CycleTimeInfo cycleTime = calculateCycleTime(task);
            if (cycleTime == null) {
                log.warn("automaticJudgeResult 计算周期时间失败，跳过任务，taskId：{}", task.getTaskId());
                return;
            }

            // 2. 获取或创建执行记录
            ExecutionRecordInfo executionInfo = getOrCreateExecutionRecord(task, cycleTime, executionMap);
            if (executionInfo == null || executionInfo.getExecutionId() == null) {
                log.warn("automaticJudgeResult 获取执行记录失败，跳过任务，taskId：{}", task.getTaskId());
                return;
            }

            // 3. 检查周期是否结束
            if (isCycleNotEnded(executionInfo.getExecution(), cycleTime.getEndTime())) {
                log.info("automaticJudgeResult 当前周期尚未结束，不生成统计与通知，taskId：{}，executionId：{}",
                        task.getTaskId(), executionInfo.getExecutionId());
                return;
            }

            // 4. 查询并处理AI研判结果
            JudgeResultInfo judgeResult = queryAndProcessJudgeResult(executionInfo.getExecutionId());

            // 5. 生成通知并创建下一周期记录
            if (shouldGenerateNotification(executionInfo.getExecution())) {
                generateNotificationAndNextCycle(task, executionInfo, judgeResult, cycleTime);
            }
        } catch (Exception e) {
            log.error("automaticJudgeResult 处理任务异常，taskId：{}，userId：{}",
                    task.getTaskId(), task.getUserId(), e);
        }
    }

    /**
     * 计算周期时间
     */
    private CycleTimeInfo calculateCycleTime(OperationTaskVO task) {
        int freqValue = calculateFrequencyInSeconds(task);
        if (freqValue <= 0) {
            log.warn("automaticJudgeResult 计算频次失败，freqValue：{}", freqValue);
            return null;
        }

        Date taskStartTime = DateUtils.string2Date(task.getStartTime());
        if (taskStartTime == null) {
            log.warn("automaticJudgeResult 任务开始时间解析失败，taskId：{}", task.getTaskId());
            return null;
        }

        int currentCycle = calculateCurrentCycle(taskStartTime, freqValue);
        int startSeconds = freqValue * (currentCycle - 1);
        Date startTime = addSeconds(taskStartTime, startSeconds);
        Date endTime = addSeconds(startTime, freqValue);

        log.info("automaticJudgeResult 计算周期频次完成，freqValue(秒)：{}，taskStartTime：{}，currentCycle：{}，startSeconds：{}，startTime：{}，endTime：{}",
                freqValue, taskStartTime, currentCycle, startSeconds, startTime, endTime);

        return new CycleTimeInfo(freqValue, startTime, endTime);
    }

    /**
     * 获取或创建执行记录
     */
    private ExecutionRecordInfo getOrCreateExecutionRecord(OperationTaskVO task, CycleTimeInfo cycleTime,
                                                           Map<Long, VmJudgeTaskExecutionDO> executionMap) {
        VmJudgeTaskExecutionDO execution = executionMap.get(task.getTaskId());

        if (execution == null) {
            // 首次创建执行记录
            execution = createNewExecutionRecord(task, cycleTime);
            Long executionId = extractedInsert(execution);
            log.info("automaticJudgeResult 首次生成执行记录，executionId：{}", executionId);
            return new ExecutionRecordInfo(execution, executionId);
        } else {
            // 使用已有执行记录
            Long executionId = execution.getExecutionId();
            log.info("automaticJudgeResult 使用已有执行记录，executionId：{}", executionId);
            return new ExecutionRecordInfo(execution, executionId);
        }
    }

    /**
     * 创建新的执行记录
     */
    private VmJudgeTaskExecutionDO createNewExecutionRecord(OperationTaskVO task, CycleTimeInfo cycleTime) {
        VmJudgeTaskExecutionDO execution = new VmJudgeTaskExecutionDO();
        execution.setUserId(task.getUserId());
        execution.setGroupId(task.getGroupId());
        execution.setTaskId(task.getTaskId());
        execution.setStartTime(cycleTime.getStartTime());
        execution.setEndTime(cycleTime.getEndTime());
        return execution;
    }

    /**
     * 检查周期是否未结束
     */
    private boolean isCycleNotEnded(VmJudgeTaskExecutionDO execution, Date calculatedEndTime) {
        Date currentTime = new Date();
        Date executionEndTime = execution != null ? execution.getEndTime() : null;

        // 如果执行记录为空且计算的结束时间大于当前时间，则周期未结束
        if (execution == null && calculatedEndTime != null && calculatedEndTime.after(currentTime)) {
            return true;
        }

        // 如果执行记录的结束时间大于当前时间，则周期未结束
        return executionEndTime != null && executionEndTime.after(currentTime);
    }

    /**
     * 查询并处理AI研判结果
     */
    private JudgeResultInfo queryAndProcessJudgeResult(Long executionId) {
        AiJudgeResultDTO aiJudgeResultDto = new AiJudgeResultDTO();
        aiJudgeResultDto.setTaskExecutionIdList(Collections.singletonList(executionId));

        Result<List<AiJudgeAlarmVO>> listResult = iTowerEventWarningService.selectAiJudgeResultList(aiJudgeResultDto);

        VmJudgeTaskExecutionDO updateExecution = new VmJudgeTaskExecutionDO();
        Date earliestDate = null;
        Date latestDate = null;

        if (listResult != null && listResult.getCode() == 200 && !CollectionUtils.isEmpty(listResult.getData())) {
            List<AiJudgeAlarmVO> dataList = listResult.getData();
            log.info("automaticJudgeResult 查询到AI研判结果数量：{}，executionId：{}", dataList.size(), executionId);

            updateExecution = judgeResultAggregation(dataList);
            earliestDate = dataList.stream()
                    .map(AiJudgeAlarmVO::getCreateTime)
                    .filter(Objects::nonNull)
                    .min(Date::compareTo)
                    .orElse(null);
            latestDate = dataList.stream()
                    .map(AiJudgeAlarmVO::getCreateTime)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElse(null);
        } else {
            log.info("automaticJudgeResult 未查询到AI研判结果或接口返回非成功，executionId：{}，listResult：{}",
                    executionId, listResult == null ? "null" : JSON.toJSONString(listResult));
            updateExecution.setAiJudgeTotal(0);
            updateExecution.setRealNum(0);
            updateExecution.setFalseNum(0);
            updateExecution.setUnJudgedNum(0);
        }

        return new JudgeResultInfo(updateExecution, earliestDate, latestDate);
    }

    /**
     * 判断是否应该生成通知
     */
    private boolean shouldGenerateNotification(VmJudgeTaskExecutionDO execution) {
        return execution != null
                && execution.getEndTime() != null
                && execution.getEndTime().before(new Date());
    }

    /**
     * 生成通知并创建下一周期记录
     */
    private void generateNotificationAndNextCycle(OperationTaskVO task, ExecutionRecordInfo executionInfo,
                                                  JudgeResultInfo judgeResult, CycleTimeInfo cycleTime) {
        VmJudgeTaskExecutionDO execution = executionInfo.getExecution();
        Long executionId = executionInfo.getExecutionId();
        VmJudgeTaskExecutionDO updateExecution = judgeResult.getUpdateExecution();

        log.info("automaticJudgeResult 生成通知信息并放到执行记录表，执行记录数据：{}", JSONUtil.toJsonStr(execution));

        // 设置研判时间
        String judgeTime = formatJudgeTime(judgeResult.getEarliestDate(), judgeResult.getLatestDate());
        updateExecution.setJudgeTime(judgeTime);

        // 生成通知内容
        String content = null;
        try {
            content = generateDescriptionWithTemplate(Constants.AUTO_JUDGE_RESULT_NOTICE, updateExecution);
        } catch (Exception e) {
            log.error("automaticJudgeResult 生成通知内容异常，taskId：{}，userId：{}，executionId：{}",
                    task.getTaskId(), task.getUserId(), executionId, e);
            // 即使生成内容失败，也继续后续流程，使用默认内容
            content = "自动研判结果通知";
        }

        // 发送通知给小塔（异常不影响后续流程）
        Result<String> notifyResult = null;
        try {
            notifyResult = sendNotificationToXiaota(task.getUserId(), executionId, content);
        } catch (Exception e) {
            log.error("automaticJudgeResult 发送通知给小塔异常，taskId：{}，userId：{}，executionId：{}，不影响其他用户通知",
                    task.getTaskId(), task.getUserId(), executionId, e);
            notifyResult = Result.fail("通知发送异常：" + e.getMessage());
        }

        // 记录通知（异常不影响后续流程）
        try {
            recordNotify(execution, content, notifyResult);
        } catch (Exception e) {
            log.error("automaticJudgeResult 记录通知异常，taskId：{}，userId：{}，executionId：{}，不影响其他用户通知",
                    task.getTaskId(), task.getUserId(), executionId, e);
        }

        // 更新执行记录（异常不影响后续流程）
        try {
            updateExecution.setExecutionLog(content);
            updateExecution.setExecutionId(executionId);
            extractedUpdate(updateExecution);
        } catch (Exception e) {
            log.error("automaticJudgeResult 更新执行记录异常，taskId：{}，userId：{}，executionId：{}，不影响其他用户通知",
                    task.getTaskId(), task.getUserId(), executionId, e);
        }

        // 创建下一周期执行记录（异常不影响其他用户）
        try {
            createNextCycleExecutionRecord(task, cycleTime);
            log.info("automaticJudgeResult 已生成下一周期执行记录，taskId：{}，userId：{}",
                    task.getTaskId(), task.getUserId());
        } catch (Exception e) {
            log.error("automaticJudgeResult 创建下一周期执行记录异常，taskId：{}，userId：{}，不影响其他用户通知",
                    task.getTaskId(), task.getUserId(), e);
        }
    }

    /**
     * 格式化研判时间
     */
    private String formatJudgeTime(Date earliestDate, Date latestDate) {
        String startTimeStr = formatTimeDay(earliestDate);
        String endTimeStr = formatTimeDay(latestDate);
        return startTimeStr.equals(endTimeStr) ? startTimeStr : startTimeStr + "至" + endTimeStr;
    }

    /**
     * 发送通知给小塔
     *
     * @param userId      用户ID
     * @param executionId 执行记录ID
     * @param content     通知内容
     * @return 发送结果
     */
    private Result<String> sendNotificationToXiaota(String userId, Long executionId, String content) {
        try {
            Map<String, String> notifyMap = new HashMap<>();
            notifyMap.put("status", Constants.ZERO);
            notifyMap.put("msg", content);
            notifyMap.put("executionId", String.valueOf(executionId));

            WebSocketMsgType msgType = WebSocketMsgType.AUTOMATIC_JUDGE_RESULT_NOTICE;
            Result<String> notifyResult = processOperationJudgeNotificationAsync(
                    userId, JSONObject.toJSONString(notifyMap), msgType);

            log.info("automaticJudgeResult 通知小塔发送完成，userId：{}，executionId：{}，发送结果：{}",
                    userId, executionId, notifyResult == null ? "null" : JSON.toJSONString(notifyResult));

            return notifyResult != null ? notifyResult : Result.fail("通知发送失败");
        } catch (Exception e) {
            log.error("automaticJudgeResult 发送通知给小塔内部异常，userId：{}，executionId：{}",
                    userId, executionId, e);
            throw e; // 重新抛出异常，由上层捕获处理
        }
    }

    /**
     * 创建下一周期执行记录
     */
    private void createNextCycleExecutionRecord(OperationTaskVO task, CycleTimeInfo cycleTime) {
        VmJudgeTaskExecutionDO nextExecution = new VmJudgeTaskExecutionDO();
        nextExecution.setUserId(task.getUserId());
        nextExecution.setGroupId(task.getGroupId());
        nextExecution.setTaskId(task.getTaskId());
        nextExecution.setStartTime(cycleTime.getStartTime());
        nextExecution.setEndTime(cycleTime.getEndTime());
        extractedInsert(nextExecution);
    }

    /**
     * 记录通知
     *
     * @param vmJudgeTaskExecution 执行记录
     * @param content              通知内容
     * @param notifyResult         通知结果
     */
    private void recordNotify(VmJudgeTaskExecutionDO vmJudgeTaskExecution, String content, Result<String> notifyResult) {
        try {
            AgentOperationTaskReminderDO reminder = new AgentOperationTaskReminderDO();
            reminder.setExecutionId(vmJudgeTaskExecution.getExecutionId());
            reminder.setTaskId(vmJudgeTaskExecution.getTaskId());
            reminder.setUserId(vmJudgeTaskExecution.getUserId());
            reminder.setNotifyType(Constants.TWO);
            reminder.setNotifyContent(content);
            reminder.setSendStatus(notifyResult != null && notifyResult.getCode() == 200 ? 1 : 2);
            reminder.setSendTime(new Date());
            reminder.setCreateTime(new Date());
            agentOperationTaskReminderDAO.insert(reminder);
        } catch (Exception e) {
            log.error("automaticJudgeResult 记录通知内部异常，executionId：{}，userId：{}",
                    vmJudgeTaskExecution != null ? vmJudgeTaskExecution.getExecutionId() : "null",
                    vmJudgeTaskExecution != null ? vmJudgeTaskExecution.getUserId() : "null", e);
            throw e; // 重新抛出异常，由上层捕获处理
        }
    }

    @Override
    public void timeoutJudgeResult(String param) {
        try {
            // 1、查询未开启自动执行任务
            AlarmOperateTaskDTO taskRequest = new AlarmOperateTaskDTO();
            Map<String, Object> taskStatus = new HashMap<>();
            taskStatus.put("taskStatus", Constants.AI_JUDGE_TASK_STATUS_CLOSE);
            taskRequest.setInputs(taskStatus);
            taskRequest.setSingleLabel("999999");
            Result<List<OperationTaskVO>> taskResult = iAlarmOperateTaskService.getTaskList(taskRequest);
            log.info("====超期研判执行结果通知，任务列表===={}", JSON.toJSONString(taskResult));
            List<String> closeTaskUser = null;
            if (taskResult != null && taskResult.getCode() == ResultCode.SUCCESS.getCode()) {
                List<OperationTaskVO> data = taskResult.getData();
                if (!CollectionUtils.isEmpty(data)) {
                    closeTaskUser = data.stream().map(OperationTaskVO::getUserId).collect(Collectors.toList());
                }
            }
            // 2、查询最新的执行完成执行记录信息
            List<VmJudgeTaskExecutionDO> executionDOList = vmAgentOperationTaskExecutionDAO.selectByUserIds(closeTaskUser, Constants.TWO_NUM, null);
            log.info("====查询最新的执行完成执行记录，记录列表===={}", JSON.toJSONString(executionDOList));
            if (!CollectionUtils.isEmpty(executionDOList)) {
                List<Long> excutionIdList = executionDOList.stream().map(VmJudgeTaskExecutionDO::getExecutionId).collect(Collectors.toList());
                List<VmJudgeTaskExecutionDO> executionNotifyList = null;
                // 3、根据执行记录查询是否有未处理的研判数据
                AiJudgeResultDTO aiJudgeResultDto = new AiJudgeResultDTO();
                aiJudgeResultDto.setTaskExecutionIdList(excutionIdList);
                Result<List<AiJudgeAlarmVO>> listResult = iTowerEventWarningService.selectAiJudgeResultList(aiJudgeResultDto);
                if (listResult != null && listResult.getCode() == 200) {
                    List<AiJudgeAlarmVO> dataList = listResult.getData();
                    executionNotifyList = getDuplicates(executionDOList, dataList);
                }
                if (!CollectionUtils.isEmpty(executionNotifyList)) {
                    // 根据未处理告警获取已发送的提醒记录
                    List<Long> historyList = executionNotifyList.stream().map(VmJudgeTaskExecutionDO::getExecutionId).collect(Collectors.toList());
                    List<AgentOperationTaskReminderDO> reminderDOList = agentOperationTaskReminderDAO.selectReminderList(historyList);
                    log.info("====查询最新的执行完成执行记录，记录列表===={}", JSON.toJSONString(reminderDOList));
                    //检查超期情况并发送通知
                    checkTaskOverdueReminder(executionNotifyList, reminderDOList);
                }

            }
        } catch (Exception e) {
            log.error("超期结果通知异常", e);
        }
    }

    @Override
    public void syncAlgorithmWeight(String param) {
        try {
            // 1、从大数据平台获取算法权重数据
            Map<String, Object> paramsPri = new HashMap<>();
            JSONObject jsonObject = bigDataPlatformHttpUtil.doPost(param, JSON.toJSONString(paramsPri));
            List<VmAlgorithmWeightDO> list = JSONObject.parseArray(jsonObject.getString("rows"), VmAlgorithmWeightDO.class);

            if (CollectionUtils.isEmpty(list)) {
                log.info("从大数据平台获取的算法权重数据为空");
                return;
            }

            log.info("从大数据平台获取算法权重数据，共{}条", list.size());

            // 2、提取算法编码列表，查询数据库中已存在的算法
            List<String> algorithmCodeList = list.stream()
                    .map(VmAlgorithmWeightDO::getAlgorithmCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(algorithmCodeList)) {
                log.warn("算法编码列表为空，跳过同步");
                return;
            }

            List<VmAlgorithmWeightDO> existingAlgorithmList = algorithmWeightDAO.selectAlgorithmCodeList(algorithmCodeList);

            // 3、构建已存在算法的编码集合，用于快速判断
            Set<String> existingAlgorithmCodeSet = existingAlgorithmList.stream()
                    .map(VmAlgorithmWeightDO::getAlgorithmCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 4、分离需要更新和新增的数据
            List<VmAlgorithmWeightDO> updateList = new ArrayList<>();
            List<VmAlgorithmWeightDO> insertList = new ArrayList<>();
            Date currentTime = new Date();

            for (VmAlgorithmWeightDO algorithmWeight : list) {
                if (algorithmWeight.getAlgorithmCode() == null) {
                    log.warn("算法编码为空，跳过该条数据：{}", JSON.toJSONString(algorithmWeight));
                    continue;
                }

                // 根据算法编码判断是更新还是新增
                if (existingAlgorithmCodeSet.contains(algorithmWeight.getAlgorithmCode())) {
                    // 已存在，加入更新列表
                    updateList.add(algorithmWeight);
                } else {
                    // 不存在，加入新增列表，设置创建时间和更新时间
                    algorithmWeight.setCreateTime(currentTime);
                    algorithmWeight.setUpdateTime(currentTime);
                    insertList.add(algorithmWeight);
                }
            }

            // 5、批量更新已存在的算法权重
            if (!CollectionUtils.isEmpty(updateList)) {
                int updateCount = algorithmWeightDAO.batchUpdateWeights(updateList);
                log.info("批量更新算法权重，共{}条，成功更新{}条", updateList.size(), updateCount);
            }

            // 6、批量新增不存在的算法权重
            if (!CollectionUtils.isEmpty(insertList)) {
                int insertCount = algorithmWeightDAO.batchUpsert(insertList);
                log.info("批量新增算法权重，共{}条，成功新增{}条", insertList.size(), insertCount);
            }

            log.info("算法权重同步完成，更新{}条，新增{}条", updateList.size(), insertList.size());

        } catch (Exception e) {
            log.error("同步算法权重异常", e);
            throw e;
        }
    }


    /**
     * 为 Date 对象添加秒数
     *
     * @param date    原始日期
     * @param seconds 要添加的秒数（可为负数）
     * @return 添加秒数后的新 Date 对象
     */
    public static Date addSeconds(Date date, long seconds) {
        if (date == null) {
            return null;
        }

        Instant instant = date.toInstant();
        Instant newInstant = instant.plusSeconds(seconds);
        return Date.from(newInstant);
    }

    /**
     * 计算当前时间属于第几个研判周期
     *
     * @param startTimeStr       开始时间字符串 (格式: yyyy-MM-dd HH:mm:ss)
     * @param frequencyInSeconds 频次周期（秒）
     * @return 当前所在的周期序号（从1开始），如果计算失败返回-1
     */
    public static int calculateCurrentCycle(Date startTimeStr, int frequencyInSeconds) {
        if (startTimeStr == null || frequencyInSeconds <= 0) {
            return -1;
        }

        try {
            // 解析开始时间
            LocalDateTime startTime = dateToLocalDateTime(startTimeStr);
            LocalDateTime currentTime = LocalDateTime.now();
            // 如果当前时间在开始时间之前，返回0
            if (currentTime.isBefore(startTime)) {
                return 0;
            }

            // 计算时间差（秒）
            Duration duration = Duration.between(startTime, currentTime);
            long secondsElapsed = duration.getSeconds();

            // 计算周期数（从1开始）
            long cycleNumber = secondsElapsed / frequencyInSeconds + 1;

            return (int) cycleNumber;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 更新统计结果（增量统计）
     */
    public static VmJudgeTaskExecutionDO judgeResultAggregation(List<AiJudgeAlarmVO> aiJudgeAlarmVOList) {
        VmJudgeTaskExecutionDO vmJudgeTaskExecutionDO = new VmJudgeTaskExecutionDO();
        int total = 0;
        int realNum = 0;
        int falseNum = 0;
        int unJudgeNum = 0;
        for (AiJudgeAlarmVO aiJudgeAlarmVO : aiJudgeAlarmVOList) {
            String aiJudgeAlarm = aiJudgeAlarmVO.getAiJudgeResult();
            if ("true".equals(aiJudgeAlarm)) {
                realNum = realNum + 1;
            } else if ("unjudged".equals(aiJudgeAlarm)) {
                unJudgeNum = unJudgeNum + 1;
            } else if ("false".equals(aiJudgeAlarm)) {
                falseNum = falseNum + 1;
            }
        }
        total = realNum + unJudgeNum + falseNum;
        vmJudgeTaskExecutionDO.setAiJudgeTotal(total);
        vmJudgeTaskExecutionDO.setRealNum(realNum);
        vmJudgeTaskExecutionDO.setFalseNum(falseNum);
        vmJudgeTaskExecutionDO.setUnJudgedNum(unJudgeNum);
        return vmJudgeTaskExecutionDO;
    }

    /**
     * 将 Date 转换为 LocalDateTime（使用系统默认时区）
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 LocalDateTime，如果输入为 null 则返回 null
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 更新执行记录
     *
     * @param updateExecution
     */
    private void extractedUpdate(VmJudgeTaskExecutionDO updateExecution) {
        updateExecution.setExecutionId(updateExecution.getExecutionId());
        updateExecution.setExecutionStatus(Constants.TWO_NUM);
        updateExecution.setUpdateTime(new Date());
        vmAgentOperationTaskExecutionDAO.updateExecution(updateExecution);
    }

    /**
     * 插入执行记录
     *
     * @param vmJudgeTaskExecutionDO
     */
    private Long extractedInsert(VmJudgeTaskExecutionDO vmJudgeTaskExecutionDO) {
        vmJudgeTaskExecutionDO.setTaskExecutionType(Constants.TWO); //任务执行类型，1 单次研判  2自动研判
        vmJudgeTaskExecutionDO.setExecutionStatus(Constants.ONE_NUM);
        vmJudgeTaskExecutionDO.setCreateTime(new Date());
        vmAgentOperationTaskExecutionDAO.insertExecution(vmJudgeTaskExecutionDO);
        return vmJudgeTaskExecutionDO.getExecutionId();
    }

    /**
     * 根据OperationTaskVO对象计算频次周期（秒）
     *
     * @param taskVO 操作任务VO对象
     * @return 频次周期（秒），如果计算失败返回-1
     */
    public static int calculateFrequencyInSeconds(OperationTaskVO taskVO) {
        if (taskVO == null) {
            return -1;
        }
        return calculateFrequencyInSeconds(taskVO.getFrequency(), taskVO.getUnit());
    }

    /**
     * 根据频次和单位计算周期时间（秒）
     *
     * @param frequency 频次
     * @param unit      单位 (h-小时, m-分钟, s-秒)
     * @return 频次周期（秒），如果计算失败返回-1
     */
    public static int calculateFrequencyInSeconds(String frequency, String unit) {
        if (frequency == null || frequency.trim().isEmpty() || unit == null) {
            return -1;
        }

        try {
            double freqValue = Double.parseDouble(frequency.trim());
            if (freqValue <= 0) {
                return -1;
            }
            int time = 0;
            switch (unit.toLowerCase()) {
                case "h":
                    time = (int) (freqValue * 3600); // 小时转秒;
                    break;
                case "m":
                    time = (int) (freqValue * 60);   // 分钟转秒
                    break;
                case "s":
                    time = (int) freqValue;          // 秒
                    break;
                default:
                    break;
            }
            return time;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 检查每个任务超期情况
     *
     * @param executionNotifyList
     */
    private void checkTaskOverdueReminder(List<VmJudgeTaskExecutionDO> executionNotifyList, List<AgentOperationTaskReminderDO> reminderDOList) {
        Map<Long, List<String>> sentReminderMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(reminderDOList)) {
            sentReminderMap = reminderDOList.stream()
                    .collect(Collectors.groupingBy(
                            AgentOperationTaskReminderDO::getExecutionId,
                            Collectors.mapping(
                                    AgentOperationTaskReminderDO::getOverdueNotifyType,
                                    Collectors.toList()
                            )
                    ));
        }
        //获取模板信息
        String oneTemplate = generateDescriptionWithTemplate(Constants.OVERDUE_ONE_JUDGE_RESULT_NOTICE, null);
        String template = generateDescriptionWithTemplate(Constants.OVERDUE_JUDGE_RESULT_NOTICE, null);
        //发送通知并记录
        for (VmJudgeTaskExecutionDO latestResult : executionNotifyList) {
            // 计算未处理时长（分钟）
            long overdueMinutes = calculateOverdueMinutes(latestResult.getEndTime());

            List<String> sentReminderTypes = sentReminderMap.getOrDefault(latestResult.getExecutionId(), new ArrayList<>());
            // 检查需要发送的提醒
            String reminderTypesToSend = checkReminderTypesToSend(overdueMinutes, sentReminderTypes);
            if (StringUtils.isEmpty(reminderTypesToSend)) {
                log.info("未处理时长未达到提醒条件，跳过发送提醒：{}", latestResult.getExecutionId());
                continue;
            }
            // 发送提醒并记录
            sendReminderAndRecord(oneTemplate, template, latestResult, reminderTypesToSend);
        }
    }

    /**
     * 发送提醒并记录
     */
    private void sendReminderAndRecord(String oneTemplate, String template, VmJudgeTaskExecutionDO latestResult, String reminderTypesToSend) {
        try {
            // 1. 生成通知内容
            String notifyContent = generateNotifyContent(oneTemplate, template, latestResult, reminderTypesToSend);

            // 2. 发送通知
            boolean sendSuccess = sendNotification(notifyContent, latestResult);

            // 3. 记录提醒
            AgentOperationTaskReminderDO reminder = new AgentOperationTaskReminderDO();
            reminder.setExecutionId(latestResult.getExecutionId());
            reminder.setTaskId(latestResult.getTaskId());
            reminder.setUserId(latestResult.getUserId());
            reminder.setNotifyType(Constants.FOUR);
            reminder.setOverdueNotifyType(reminderTypesToSend);
            reminder.setNotifyContent(notifyContent);
            reminder.setSendStatus(sendSuccess ? 1 : 2);
            reminder.setSendTime(new Date());
            reminder.setCreateTime(new Date());

            agentOperationTaskReminderDAO.insert(reminder);

            if (sendSuccess) {
                log.info("超期提醒发送成功 - 执行记录ID: {}, 类型: {}", latestResult.getExecutionId(), reminderTypesToSend);
            } else {
                log.info("超期提醒发送失败 - 执行记录ID: {}, 类型: {}", latestResult.getExecutionId(), reminderTypesToSend);
            }

        } catch (Exception e) {
            log.error("发送超期提醒异常 - 执行记录ID: {}, 错误信息: {}", latestResult.getExecutionId(), e.getMessage(), e);
        }
    }

    /**
     * 生成通知内容
     *
     * @param oneTemplate         单次任务模板
     * @param template            自动任务模板
     * @param latestResult        执行记录
     * @param reminderTypesToSend 提醒类型
     * @return 生成的通知内容
     */
    private String generateNotifyContent(String oneTemplate, String template, VmJudgeTaskExecutionDO latestResult, String reminderTypesToSend) {
        Map<String, Object> map = new HashMap<>();
        if (latestResult != null && Constants.ONE.equals(latestResult.getTaskExecutionType())) {
            // 单次任务：使用单次任务模板
            map.put("endTime", formatTime(latestResult.getEndTime()));
            map.put("reminderTypesToSend", reminderTypesToSend);
            return replacePlaceholders(oneTemplate, map);
        } else {
            // 自动任务：使用自动任务模板
            String startTime = formatTimeDay(latestResult.getStartTime());
            String endTime = formatTimeDay(latestResult.getEndTime());
            map.put("startTime", startTime.equals(endTime) ? startTime : startTime + "至" + endTime);
            map.put("reminderTypesToSend", reminderTypesToSend);
            return replacePlaceholders(template, map);
        }
    }

    /**
     * 发送通知（实际调用小塔通知接口）
     *
     * @param notifyContent 通知内容
     * @param latestResult  执行记录
     * @return 发送是否成功
     */
    private boolean sendNotification(String notifyContent, VmJudgeTaskExecutionDO latestResult) {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("msg", notifyContent);
        // 推送小塔研判结果
        WebSocketMsgType webSocketMsgType = WebSocketMsgType.TIMEOUT_JUDGE_RESULT_NOTICE;
        Result<String> notifyResult = this.processOperationJudgeNotificationAsync(latestResult.getUserId(), JSONObject.toJSONString(msgMap), webSocketMsgType);
        return notifyResult != null && notifyResult.getCode() == ResultCode.SUCCESS.getCode();
    }

    /**
     * 检查需要发送的提醒类型
     *
     * @param overdueMinutes    超期时长（分钟）
     * @param sentReminderTypes 已发送的提醒类型列表
     * @return 需要发送的提醒类型，如果没有需要发送的则返回null
     */
    private String checkReminderTypesToSend(long overdueMinutes, List<String> sentReminderTypes) {
        // 使用Stream按配置顺序遍历，找到第一个满足条件的提醒类型
        return OverdueNotifyConfig.getReminderIntervals().entrySet().stream()
                .filter(entry -> {
                    String reminderType = entry.getKey();
                    Long interval = entry.getValue();
                    // 检查是否超期且未发送过该类型的提醒
                    return interval != null
                            && overdueMinutes >= interval
                            && !sentReminderTypes.contains(reminderType);
                })
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 计算超期时长（分钟）
     */
    private long calculateOverdueMinutes(Date executeTime) {
        if (executeTime == null) {
            return 0;
        }

        long executeTimeMillis = executeTime.getTime();
        long currentTimeMillis = System.currentTimeMillis();

        return (currentTimeMillis - executeTimeMillis) / (1000 * 60);
    }

    /**
     * 通知小塔打开弹窗通知
     */
    private Result<String> processOperationJudgeNotificationAsync(String userId, String msg, WebSocketMsgType webSocketMsgType) {
        Result<String> result = null;
        try {
            OperationJudgeMsg operationJudgeMsgReq = OperationJudgeMsg.builder().userId(userId).build();
            operationJudgeMsgReq.setWebSocketMsgType(webSocketMsgType);
            operationJudgeMsgReq.setExtendInfo(JSONObject.parseObject(msg));
            result = operationJudgeMsgAPI.sendNotice(operationJudgeMsgReq);
            log.info("通知小塔打开弹窗通知，用户：{}", JSONUtil.toJsonStr(userId));
        } catch (Exception e) {
            log.error("通知小塔打开弹窗通知异常", e);
            result = Result.fail("通知小塔打开弹窗通知异常");
        }
        return result;
    }

    /**
     * 格式化时间为中文格式
     */
    private String formatTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm分");
        return sdf.format(date);
    }

    private String formatTimeDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    /**
     * 使用模板替换方式
     */
    public String generateDescriptionWithTemplate(String templateKey, Object replaceObject) {
        String template = null;
        Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType("ai_judge_operate_msg_template");
        if (dictResult.getCode() == 200 && dictResult.getData() != null && !dictResult.getData().isEmpty()) {
            for (SysDictDataResponse dictData : dictResult.getData()) {
                if ("0".equals(dictData.getStatus()) && templateKey.equals(dictData.getDictLabel())) { // 只取启用状态的字典
                    template = dictData.getDictValue();
                    break;
                }
            }
        }
        if (!StringUtils.isEmpty(template)) {
            log.info("字典配置中AI研判通知模板配置获取成功：{}，{}", templateKey, template);
        } else {
            log.info("字典配置中AI研判通知模板配置未配置：{}", templateKey);
        }
        if (replaceObject == null) {
            return template;
        }
        Map<String, Object> replacements = BeanUtil.beanToMap(replaceObject);
        return replacePlaceholders(template, replacements);
    }

    /**
     * 替换占位符
     */
    private String replacePlaceholders(String template, Map<String, Object> replacements) {
        String result = template;
        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            String placeholder = "[" + entry.getKey() + "]";
            result = result.replace(placeholder, entry.getValue() + "");
        }
        return result;
    }

    public static List<VmJudgeTaskExecutionDO> getDuplicates(
            List<VmJudgeTaskExecutionDO> taskExecutionDOList, List<AiJudgeAlarmVO> orderListForAiOperate
    ) {

        if (taskExecutionDOList == null || taskExecutionDOList.isEmpty()) {
            return new ArrayList<>();
        }

        if (orderListForAiOperate == null || orderListForAiOperate.isEmpty()) {
            return new ArrayList<>(taskExecutionDOList);
        }

        // 提取alarmAiOperateList中的warningOrderId集合
        Set<Long> taskExecutionIds = orderListForAiOperate.stream()
                .map(AiJudgeAlarmVO::getTaskExecutionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 过滤掉orderListForOperate中存在于aiWarningOrderIds的数据
        return taskExecutionDOList.stream()
                .filter(vo -> taskExecutionIds.contains(vo.getExecutionId()))
                .collect(Collectors.toList());
    }

    /**
     * 从字典获取告警类型ID列表
     *
     * @return 告警类型ID列表
     */
    private List<String> getWarningTypeIdsFromDict() {
        try {
            // 违建告警类型
            String dictType = "ai_operate_alarm_type"; // 需要根据实际字典类型调整

            Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType(dictType);
            List<String> warningTypeIdList = new ArrayList<>();

            if (dictResult.getCode() == 200 && dictResult.getData() != null && !dictResult.getData().isEmpty()) {
                for (SysDictDataResponse dictData : dictResult.getData()) {
                    if ("0".equals(dictData.getStatus())) { // 只取启用状态的字典
                        warningTypeIdList.add(dictData.getDictValue());
                    }
                }
                log.info("从字典获取告警类型ID列表：{}", warningTypeIdList);
            } else {
                // 如果字典中没有配置，使用默认值
                warningTypeIdList.add("1717113");
                log.warn("字典中未找到告警类型配置，使用默认值：{}", warningTypeIdList);
            }
            return warningTypeIdList;

        } catch (Exception e) {
            log.error("从字典获取告警类型ID列表异常", e);
            // 异常时返回默认值
            return Arrays.asList("1717113");
        }
    }

}
