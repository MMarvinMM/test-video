package com.chinatower.airline.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.response.ExportStatisticalAnalysisReq;
import com.chinatower.airline.dto.request.StatisticalAnalysisReq;
import com.chinatower.airline.dto.request.TaskStatisticsReq;
import com.chinatower.airline.dto.response.*;
import com.chinatower.airline.enums.TaskErrorReasonTypeEnum;
import com.chinatower.airline.mapper.AirStatisticsMapper;
import com.chinatower.airline.service.AirStatisticsService;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 11:00
 */
@Service
@Slf4j
public class AirStatisticsServiceImpl implements AirStatisticsService {
    @Resource
    private AirStatisticsMapper airStatisticsMapper;
    @Resource
    private MongoTemplate mongoTemplate;

    private static String TOTAL_COUNT = "totalCount";
    private static String ERROR_REASON = "errorReason";
    private static String WARNING_TYPE_ID = "warningTypeId";
    private static String ALARM_BODY = "alarmBody";
    private static String ALARM_NUM = "alarmNum";
    private static String TASK_RATE = "taskRate";
    private static String SHOW_TIME = "showTime";
    private static String DEVICE_NAME = "deviceName";

    /**
     * 统计任务数量 列表
     * @param taskStatisticsReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code",deviceColumn = "t.uav_code")
    public Result queryTaskCount(TaskStatisticsReq taskStatisticsReq) {
        if (ObjectUtil.isEmpty(taskStatisticsReq)){
            return Result.fail("入参不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        taskStatisticsReq.setUserId(loginUser.getUser().getUserId());
        taskStatisticsReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        taskStatisticsReq.setTenantId(loginUser.getUser().getTenantId());
        TaskStatisticsRsp taskStatisticsRsp = airStatisticsMapper.queryTaskCount(taskStatisticsReq);
        if (ObjectUtil.isNotEmpty(taskStatisticsRsp)){
            Integer pageNum = taskStatisticsReq.getPageNum();
            Integer pageSize = taskStatisticsReq.getPageSize();
            if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
                PageHelper.startPage(pageNum, pageSize, true, false, null);
            }
            List<AirTaskStatisticsRes> airTaskRes = airStatisticsMapper.queryTaskCountList(taskStatisticsReq);
            taskStatisticsRsp.setAirTaskRes(airTaskRes);
        }
        log.info("airStatistics-queryTaskCount-rsp{}",JSONObject.toJSON(taskStatisticsRsp));
        return Result.data(taskStatisticsRsp);
    }

    /**
     * 统计任务数量 按天/月
     * @param taskStatisticsReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code",deviceColumn = "t.uav_code")
    public Result queryTaskCountDayMonth(TaskStatisticsReq taskStatisticsReq) {
        if (ObjectUtil.isEmpty(taskStatisticsReq)){
            return Result.fail("入参不能为空");
        }
        List<Map> taskStatisticsRsp = Collections.singletonList(new HashMap<>());
        if (Constants.ZERO.equals(taskStatisticsReq.getQueryType())){
            // 按天统计
            taskStatisticsRsp = airStatisticsMapper.queryTaskCountDay(taskStatisticsReq);
            List<Map> finalTaskStatisticsRsp = taskStatisticsRsp;
            taskStatisticsRsp = IntStream.range(0, 24)  // 生成0-23小时
                    .mapToObj(hour -> {
                        String hourStr = String.format("%02d", hour);
                        Optional<Map> existingData = finalTaskStatisticsRsp.stream()
                                .filter(map -> String.valueOf(hourStr).equals(map.getOrDefault(com.chinatower.airline.constants.Constants.SHOW_TIME, -1)))
                                .findFirst();
                        return existingData.orElseGet(() -> {
                            Map<String, Object> zeroMap = new HashMap<>();
                            zeroMap.put(com.chinatower.airline.constants.Constants.SHOW_TIME, hourStr);
                            zeroMap.put(TOTAL_COUNT, 0);
                            return zeroMap;
                        });
                    })
                    .sorted(Comparator.comparing(map -> (String) map.get(com.chinatower.airline.constants.Constants.SHOW_TIME)))
                    .collect(Collectors.toList());
        }
        if (Constants.ONE.equals(taskStatisticsReq.getQueryType())){
            taskStatisticsRsp = airStatisticsMapper.queryTaskCountMonth(taskStatisticsReq);
            if (taskStatisticsReq.getStartTime() != null && taskStatisticsReq.getEndTime() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = sdf.parse(taskStatisticsReq.getStartTime());
                    Date endDate = sdf.parse(taskStatisticsReq.getEndTime());
                    Map<String, Map<String, Object>> dateMap = taskStatisticsRsp.stream()
                            .collect(Collectors.toMap(
                                    map -> {
                                        Object showTime = map.get(com.chinatower.airline.constants.Constants.SHOW_TIME);
                                        if (showTime instanceof java.sql.Date) {
                                            return sdf.format((java.sql.Date) showTime);
                                        }
                                        return String.valueOf(showTime);
                                    },
                                    map -> map
                            ));
                    List<Map> filledData = new ArrayList<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);

                    while (!calendar.getTime().after(endDate)) {
                        String dateStr = sdf.format(calendar.getTime());
                        filledData.add(
                                dateMap.getOrDefault(dateStr, createZeroDayData(dateStr))
                        );
                        calendar.add(Calendar.DATE, 1);
                    }
                    taskStatisticsRsp = filledData;
                } catch (DateTimeParseException | ParseException e) {
                    log.error("日期格式错误{}", JSONObject.toJSON(e));
                }
            }
        }
        log.info("airStatistics-queryTaskCountDayMonth-rsp{}",JSONObject.toJSON(taskStatisticsRsp));
        return Result.data(taskStatisticsRsp);
    }

    @Override
    public Result queryStatisticsOverview(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        Map map = airStatisticsMapper.queryStatisticsOverview(statisticalAnalysisReq);
        log.info("airStatistics-queryTaskCountDayMonth-rsp{}",JSONObject.toJSON(map));
        return Result.data(map);
    }

    @Override
    public Result queryFlightDistanceTop(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryFlightDistanceTop(statisticalAnalysisReq);
        log.info("airStatistics-queryFlightDistanceTop-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public Result queryFlightDurationTop(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryFlightDurationTop(statisticalAnalysisReq);
        log.info("airStatistics-queryFlightDurationTop-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public Result queryFailCauseTop(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        ArrayList<Map> respList = getFailCauseTop(statisticalAnalysisReq);
        log.info("airStatistics-queryFailCauseTop-rsp{}",JSONObject.toJSON(respList));
        return Result.data(respList);
    }

    private ArrayList<Map> getFailCauseTop(StatisticalAnalysisReq statisticalAnalysisReq) {
        List<String> list = airStatisticsMapper.queryFailCauseTop(statisticalAnalysisReq);
        // 按错误类型分组统计
        Map<String, Integer> typeCountMap = new HashMap<>();

        for (String errorReason : list) {
            TaskErrorReasonTypeEnum errorType = TaskErrorReasonTypeEnum.getByErrorReason(errorReason);
            typeCountMap.merge(errorType.getDescription(), 1, Integer::sum);
        }

        ArrayList<Map> respList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put(ERROR_REASON, entry.getKey());
            map.put(TOTAL_COUNT, entry.getValue());
            respList.add(map);
        }
        respList.sort(Comparator.comparing(
                o -> (Integer) o.get(TOTAL_COUNT),
                Comparator.reverseOrder()  // 指定降序
        ));
        return respList;
    }

    @Override
    public Result queryPlanUavFlyCountTop(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryPlanUavFlyCountTop(statisticalAnalysisReq);
        log.info("airStatistics-queryPlanUavFlyCountTop-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public Result queryTaskStatusStatistics(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        Map map = airStatisticsMapper.queryTaskStatusStatistics(statisticalAnalysisReq);
        log.info("airStatistics-queryTaskStatusStatistics-rsp{}",JSONObject.toJSON(map));
        return Result.data(map);
    }

    @Override
    public Result queryFlyTypeStatistics(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryFlyTypeStatistics(statisticalAnalysisReq);
        log.info("airStatistics-queryFlyTypeStatistics-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public Result queryFlyTrend(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryFlyTrend(statisticalAnalysisReq);
        log.info("airStatistics-queryFlyTrend-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public Result queryFlyTimeStatistical(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        List<Map> list = airStatisticsMapper.queryFlyTimeStatistical(statisticalAnalysisReq);
        log.info("airStatistics-queryFlyTimeStatistical-rsp{}",JSONObject.toJSON(list));
        return Result.data(list);
    }

    @Override
    public List<Map> queryTaskDetail(StatisticalAnalysisReq statisticalAnalysisReq) {
        //查询任务数据清单
        List<Map> taskList = airStatisticsMapper.queryTaskDetail(statisticalAnalysisReq);
        if (CollectionUtils.isEmpty(taskList)){
            return taskList;
        }

        //查询mongo获取告警数据
        List<String> taskIdList = taskList.stream()
                .map(task -> {
                    Object planTaskId = task.get("planTaskId");
                    if (planTaskId == null) {
                        return null;
                    }
                    return planTaskId.toString();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (taskIdList.isEmpty()) {
            return taskList;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("taskRecordId").in(taskIdList));
        List<UavFlightTaskVO> taskMongoVOList = mongoTemplate.find(query, UavFlightTaskVO.class, com.chinatower.airline.constants.Constants.UAV_FLIGHT_TASKS);
        if (CollectionUtils.isEmpty(taskMongoVOList)){
            return taskList;
        }

        // 使用Map预处理mongo数据，提高查找效率
        Map<String, UavFlightTaskVO> taskMongoMap = taskMongoVOList.stream()
                .filter(vo -> vo.getTaskRecordId() != null)
                .collect(Collectors.toMap(UavFlightTaskVO::getTaskRecordId, vo -> vo, (existing, replacement) -> existing));

        // 计算最大告警类型数量(该批次任务最大的告警数量)
        int maxAlarmCount = 0;
        for (UavFlightTaskVO taskMongoVO : taskMongoVOList) {
            String algorithmInfoMapStr = taskMongoVO.getAlgorithmInfoMap();
            if (algorithmInfoMapStr != null && !algorithmInfoMapStr.trim().isEmpty()) {
                try {
                    List<Map<String, Object>> algorithmInfoMap = JSON.parseObject(algorithmInfoMapStr, new TypeToken<List<Map<String, Object>>>(){}.getType());
                    if (algorithmInfoMap != null) {
                        maxAlarmCount = Math.max(maxAlarmCount, algorithmInfoMap.size());
                    }
                } catch (Exception e) {
                    log.warn("解析algorithmInfoMap失败，taskRecordId: {}", taskMongoVO.getTaskRecordId(), e);
                }
            }
        }

        // 为所有记录添加告警信息
        for (Map<String, Object> map : taskList) {
            Object planTaskIdObj = map.get("planTaskId");
            if (planTaskIdObj == null) {
                continue;
            }

            String planTaskId = planTaskIdObj.toString();
            UavFlightTaskVO taskMongoVO = taskMongoMap.get(planTaskId);

            if (taskMongoVO != null) {
                String algorithmInfoMapStr = taskMongoVO.getAlgorithmInfoMap();
                List<Map<String, Object>> algorithmInfoMap = null;

                if (algorithmInfoMapStr != null && !algorithmInfoMapStr.trim().isEmpty()) {
                    try {
                        algorithmInfoMap = JSON.parseObject(algorithmInfoMapStr, new TypeToken<List<Map<String, Object>>>(){}.getType());
                    } catch (Exception e) {
                        log.warn("解析algorithmInfoMap失败，taskRecordId: {}", planTaskId, e);
                    }
                }

                // 添加告警信息或空值
                for (int i = 1; i <= maxAlarmCount; i++) {
                    if (algorithmInfoMap != null && i <= algorithmInfoMap.size()) {
                        Map<String, Object> alarmData = algorithmInfoMap.get(i - 1);
                        map.put(WARNING_TYPE_ID + i, alarmData.get(WARNING_TYPE_ID));
                        map.put("alarmBody" + i, alarmData.get(ALARM_BODY));
                        map.put(ALARM_NUM + i, alarmData.get(ALARM_NUM));
                    } else {
                        // 填充空值以保持字段数量一致
                        map.put(WARNING_TYPE_ID + i, "");
                        map.put(ALARM_BODY + i, "");
                        map.put(ALARM_NUM + i, "");
                    }
                }
            } else {
                // 如果没有对应的mongo记录，填充空值
                for (int i = 1; i <= maxAlarmCount; i++) {
                    map.put(WARNING_TYPE_ID + i, "");
                    map.put(ALARM_BODY + i, "");
                    map.put(ALARM_NUM + i, "");
                }
            }
        }
        return taskList;
    }

    @Override
    public ExportStatisticalAnalysisReq queryExportStatisticalAnalysis(StatisticalAnalysisReq statisticalAnalysisReq) {
        setUserAuto(statisticalAnalysisReq);
        ExportStatisticalAnalysisReq exportStatisticalAnalysisReq = new ExportStatisticalAnalysisReq();

        // 查询统计概览
        Map statisticsOverviewMap = airStatisticsMapper.queryStatisticsOverview(statisticalAnalysisReq);
        ArrayList<Map> statisticsOverviewList = new ArrayList<>();
        if (statisticsOverviewMap != null) {
            statisticsOverviewList.add(statisticsOverviewMap);
        }
        exportStatisticalAnalysisReq.setStatisticsOverviewList(statisticsOverviewList);

        // 设置飞行距离排行榜
        exportStatisticalAnalysisReq.setFlightDistanceTopList(airStatisticsMapper.queryFlightDistanceTop(statisticalAnalysisReq));

        // 设置飞行时长排行榜
        exportStatisticalAnalysisReq.setFlightDurationTopList(airStatisticsMapper.queryFlightDurationTop(statisticalAnalysisReq));

        // 设置失败原因排行榜
        exportStatisticalAnalysisReq.setFailCauseTopList(getFailCauseTop(statisticalAnalysisReq));

        // 设置计划无人机飞行次数排行榜
        exportStatisticalAnalysisReq.setPlanUavFlyCountTopList(airStatisticsMapper.queryPlanUavFlyCountTop(statisticalAnalysisReq));

        // 查询任务状态统计
        Map taskStatusStatisticsTopMap = airStatisticsMapper.queryTaskStatusStatistics(statisticalAnalysisReq);
        ArrayList<Map> taskStatusStatisticsTopList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskStatusStatisticsTopMap)) {
            Integer totalCount = getIntegerValue(taskStatusStatisticsTopMap, TOTAL_COUNT, 0);
            Integer successCount = getIntegerValue(taskStatusStatisticsTopMap, "successCount", 0);
            Integer waitingCount = getIntegerValue(taskStatusStatisticsTopMap, "waitingCount", 0);
            Integer runningCount = getIntegerValue(taskStatusStatisticsTopMap, "runningCount", 0);
            Integer failCount = getIntegerValue(taskStatusStatisticsTopMap, "failCount", 0);
            Integer invalidCount = getIntegerValue(taskStatusStatisticsTopMap, "invalidCount", 0);

            // 定义状态配置
            String[][] statusConfig = {
                    {"执行成功", successCount.toString()},
                    {"待执行", waitingCount.toString()},
                    {"执行中", runningCount.toString()},
                    {"执行失败", failCount.toString()},
                    {"已失效", invalidCount.toString()}
            };

            for (String[] status : statusConfig) {
                Map statusMap = new HashMap();
                statusMap.put("taskType", status[0]);
                Long count = Long.valueOf(status[1]);
                statusMap.put("taskCount", count);
                // 避免除零错误
                String taskRate = totalCount != 0 ? doubleToPercentage(count * 1.0 / totalCount) : null;
                statusMap.put(TASK_RATE, taskRate);
                taskStatusStatisticsTopList.add(statusMap);
            }
        }
        exportStatisticalAnalysisReq.setTaskStatusStatisticsTopList(taskStatusStatisticsTopList);

        // 设置飞行类型统计列表
        List<Map> flyTypeStatisticsList = airStatisticsMapper.queryFlyTypeStatistics(statisticalAnalysisReq);
        if (!CollectionUtils.isEmpty(flyTypeStatisticsList)) {
            Long totalSum = flyTypeStatisticsList.stream()
                    .mapToLong(map -> {
                        Object totalCountObj = map.get(TOTAL_COUNT);
                        return totalCountObj != null ? (Long) totalCountObj : 0;
                    })
                    .sum();

            for (Map<String, Object> flyTypeStatisticsMap : flyTypeStatisticsList) {
                Object totalCountObj = flyTypeStatisticsMap.get(TOTAL_COUNT);
                Long totalCountOne = totalCountObj != null ? (Long) totalCountObj : 0;
                flyTypeStatisticsMap.put(TASK_RATE, totalSum != 0 ? doubleToPercentage(totalCountOne * 1.0 / totalSum) : 0.0);
            }
        }

        exportStatisticalAnalysisReq.setFlyTypeStatisticsList(flyTypeStatisticsList);

        // 设置飞行趋势列表
        exportStatisticalAnalysisReq.setFlyTrendList(airStatisticsMapper.queryFlyTrend(statisticalAnalysisReq));

        // 设置飞行时间统计列表
        List<Map> maps = airStatisticsMapper.queryFlyTimeStatistical(statisticalAnalysisReq);
        maps.forEach(map -> map.compute(SHOW_TIME, (k, showTime) -> Objects.nonNull(showTime) ? showTime + ":00" : ""));
        exportStatisticalAnalysisReq.setFlyTimeStatisticalList(maps);

        // 设置任务详情列表
        exportStatisticalAnalysisReq.setTaskDetailList(queryTaskDetail(statisticalAnalysisReq));

        return exportStatisticalAnalysisReq;
    }

    /**
     * 将Double值转换为百分比字符串（保留两位小数）
     * @param value Double值，如0.428571
     * @return 百分比字符串，如"42.86%"
     */
    public static String doubleToPercentage(Double value) {
        if (value == null) {
            return "0.00%";
        }
        // 将小数转换为百分比（乘以100）
        double percentage = value * 100;
        return String.format("%.2f%%", percentage);
    }

    @Override
    public void exportStatisticalAnalysis(StatisticalAnalysisReq req, HttpServletResponse response) {
        try {
            // 查询数据
            ExportStatisticalAnalysisReq exportData = queryExportStatisticalAnalysis(req);

            // 设置响应头
            String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "飞行管理大屏统计分析清单.xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 导出Excel
            exportToExcel(exportData, response.getOutputStream());

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    // 辅助方法：安全获取Integer值
    // 修复 getIntegerValue 方法，支持多种数值类型转换为 Integer
    private Integer getIntegerValue(Map map, String key, Integer defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.valueOf(value.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }




    public void exportToExcel(ExportStatisticalAnalysisReq exportData, OutputStream outputStream) throws Exception {
        // 创建Workbook
        Workbook workbook = new XSSFWorkbook();

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // Sheet 1: 统计总览
        createStatisticsOverviewSheet(workbook, exportData.getStatisticsOverviewList(), headerStyle);

        // Sheet 2: 飞行距离统计排名
        createFlightDistanceRankSheet(workbook, exportData.getFlightDistanceTopList(), headerStyle);

        // Sheet 3: 飞行时长排名
        createFlightDurationRankSheet(workbook, exportData.getFlightDurationTopList(), headerStyle);

        // Sheet 4: 失败原因排行
        createFailCauseRankSheet(workbook, exportData.getFailCauseTopList(), headerStyle);

        // Sheet 5: 计划统计排名
        createPlanStatisticsRankSheet(workbook, exportData.getPlanUavFlyCountTopList(), headerStyle);

        // Sheet 6: 任务状态统计
        createTaskStatusStatisticsSheet(workbook, exportData.getTaskStatusStatisticsTopList(), headerStyle);

        // Sheet 7: 飞行趋势统计
        createFlightTrendSheet(workbook, exportData.getFlyTrendList(), headerStyle);

        // Sheet 8: 起飞时刻统计
        createTakeoffTimeSheet(workbook, exportData.getFlyTimeStatisticalList(), headerStyle);

        // Sheet 9: 飞行任务明细表
        createFlightTaskDetailSheet(workbook, exportData.getTaskDetailList(), headerStyle);

        // Sheet 10: 飞行类别统计
        createFlightCategorySheet(workbook, exportData.getFlyTypeStatisticsList(), headerStyle);

        // 写入输出流
        workbook.write(outputStream);
        workbook.close();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    // Sheet 1: 统计总览
    private void createStatisticsOverviewSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("统计总览");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"无人机使用设备数（个）", "已执行飞行计划（个）", "飞行次数（次）", "飞行距离（m）", "飞行时常（min）"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        if (dataList != null && !dataList.isEmpty()) {
            Map data = dataList.get(0);
            Row dataRow = sheet.createRow(1);

            dataRow.createCell(0).setCellValue(getStringValue(data.get("uavUsageCount")));
            dataRow.createCell(1).setCellValue(getStringValue(data.get("executedPlanCount")));
            dataRow.createCell(2).setCellValue(getStringValue(data.get("flightCount")));
            dataRow.createCell(3).setCellValue(getStringValue(data.get("totalDistance")));
            dataRow.createCell(4).setCellValue(getStringValue(data.get("totalDuration")));
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 2: 飞行距离统计排名
    private void createFlightDistanceRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行距离统计排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "设备名称", "飞行距离（m）"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("airMileage")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 3: 飞行时长排名
    private void createFlightDurationRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行时长排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "设备名称", "飞行时长"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("flyTime")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 4: 失败原因排行
    private void createFailCauseRankSheet(Workbook workbook, List<Map> dataList,
                                          CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("失败原因排行");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "失败原因", "次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get("errorReason")));
                dataRow.createCell(2).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 5: 计划统计排名
    private void createPlanStatisticsRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("计划统计排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"排行", "无人机名称", "计划名称", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("planName")));
                dataRow.createCell(3).setCellValue(getStringValue(data.get("flyCount")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 6: 任务状态统计
    private void createTaskStatusStatisticsSheet(Workbook workbook, List<Map> dataList,
                                                 CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("任务状态统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"任务状态", "数量", "占比"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("taskType")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get("taskCount")));
                dataRow.createCell(2).setCellValue(getStringValue(data.get(TASK_RATE)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 7: 飞行趋势统计
    private void createFlightTrendSheet(Workbook workbook, List<Map> dataList,
                                        CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行趋势统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"时间", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get(SHOW_TIME)));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 8: 起飞时刻统计
    private void createTakeoffTimeSheet(Workbook workbook, List<Map> dataList,
                                        CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("起飞时刻统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"起飞时刻", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get(SHOW_TIME)));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 9: 飞行任务明细表
    private void createFlightTaskDetailSheet(Workbook workbook, List<Map> dataList,
                                             CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行任务明细表");

        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "序号", "设备名称", "设备编码", "飞行开始时间（时分秒）", "飞行结束时间（时分秒）",
                "计划名称", "任务状态", "执行时间", "飞行类型", "航线名称", "飞行次数",
                "飞行时长", "飞行里程（m）"
        };

        //设置动态表头
        int algorithmCount = 0;
        if (!CollectionUtils.isEmpty(dataList)){
            Map firstItem = dataList.get(0);
            int dataSize = firstItem.size();
            int headerSize = headers.length;

            if (dataSize > headerSize) {
                int diff = dataSize - headerSize;
                algorithmCount = diff / 3; // 每个算法占用3列：名称、编码、告警数量

                List<String> newHeaders = new ArrayList<>(Arrays.asList(headers));
                for (int i = 1; i <= algorithmCount; i++) {
                    newHeaders.add("算法" + i + "名称");
                    newHeaders.add("算法" + i + "编码");
                    newHeaders.add("算法" + i + "告警数量");
                }
                headers = newHeaders.toArray(new String[0]);
            }
        }


        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map<String, Object> data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                int colIndex = 0;

                // 填充固定列数据
                dataRow.createCell(colIndex++).setCellValue(index++);
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("uavCode")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyStartTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyEndTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("planName")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("taskStatus")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("realityTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flightMainType")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flightRouteName")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyCount")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("accumulateMileage")));

                // 动态填充算法相关列数据
                for (int i = 1; i <= algorithmCount; i++) {
                    String algorithmName = getStringValue(data.get(ALARM_BODY + i));
                    String algorithmCode = getStringValue(data.get(WARNING_TYPE_ID + i));
                    String alarmCount = getStringValue(data.get(ALARM_NUM + i ));

                    // 检查是否有算法数据
                    if (!algorithmName.isEmpty() || !algorithmCode.isEmpty() || !alarmCount.isEmpty()) {
                        dataRow.createCell(colIndex++).setCellValue(algorithmName);
                        dataRow.createCell(colIndex++).setCellValue(algorithmCode);
                        dataRow.createCell(colIndex++).setCellValue(alarmCount);
                    } else {
                        // 如果没有算法数据，跳过这3列
                        dataRow.createCell(colIndex++).setCellValue("");
                        dataRow.createCell(colIndex++).setCellValue("");
                        dataRow.createCell(colIndex++).setCellValue("");
                    }
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 10: 飞行类别统计
    private void createFlightCategorySheet(Workbook workbook, List<Map> dataList,
                                           CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行类别统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"飞行类别", "数量", "占比"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("flightMainType")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get(TASK_RATE)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    private String getStringValue(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }


    /**
     * 设置用户信息
     * @param statisticalAnalysisReq
     */
    private void setUserAuto(StatisticalAnalysisReq statisticalAnalysisReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        statisticalAnalysisReq.setUserId(loginUser.getUser().getUserId());
        statisticalAnalysisReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        statisticalAnalysisReq.setTenantId(loginUser.getUser().getTenantId());
    }

    private Map<String, Object> createZeroDayData(String date) {
        Map<String, Object> zeroMap = new HashMap<>();
        zeroMap.put(com.chinatower.airline.constants.Constants.SHOW_TIME, date);
        zeroMap.put(TOTAL_COUNT, 0);
        return zeroMap;
    }

}
