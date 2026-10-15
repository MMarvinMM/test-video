package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec V2.0：客户统计双维度 — TYPE 利旧；SUB_TASK 读 vm_statistics_event_sub_scene。
 */
public class StatDimensionContractTest {

    @Test
    public void analysisAndStatisticsVoShouldDeclareStatDimension() throws Exception {
        Path analysisVo = resolveApi(
                "src/main/java/com/chinatower/analysis/api/entity/AnalysisVO.java");
        Path statisticsVo = resolveApi(
                "src/main/java/com/chinatower/analysis/api/entity/StatisticsVO.java");
        Path eventStatVo = resolveApi(
                "src/main/java/com/chinatower/analysis/api/entity/EventStatisticParamVO.java");
        assertTrue(readUtf8(analysisVo).contains("statDimension"));
        assertTrue(readUtf8(statisticsVo).contains("statDimension"));
        assertTrue(readUtf8(eventStatVo).contains("statDimension"));
    }

    @Test
    public void workbenchTypeQueriesShouldSwitchToSubSceneTable() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        assertTrue("应定义下游任务汇总表切换片段",
                content.contains("id=\"statEventTypeTable\"")
                        && content.contains("vm_statistics_event_sub_scene"));
        assertTrue("排行查询应 include 表切换片段",
                extractSelectBlock(content, "getInitDevTypeYearMonth").contains("statEventTypeTable"));
        assertTrue("准确率查询应 include 表切换片段",
                extractSelectBlock(content, "getEvenTypeAccuracy").contains("statEventTypeTable"));
        assertTrue("APP 类型排行应 include 表切换片段",
                extractSelectBlock(content, "getInitDevTypeYearMonthApp").contains("statEventTypeTable"));
    }

    @Test
    public void bigScreenTypeStatisticsShouldSupportSubTask() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/statistic/BigScreenDAO.xml");
        String content = readUtf8(xml);
        assertTrue(extractSelectBlock(content, "getEventTypeStatistics").contains("SUB_TASK")
                || extractSelectBlock(content, "getEventTypeStatistics")
                .contains("vm_statistics_event_sub_scene"));
        assertTrue(extractSelectBlock(content, "getAllEventType").contains("SUB_TASK")
                || extractSelectBlock(content, "getAllEventType")
                .contains("vm_statistics_event_sub_scene"));
    }

    /**
     * Spec：下游任务排行点进详情 — getEventDetailByType / getEventNumListApp 必须走 sub_scene。
     */
    @Test
    public void detailAndTrendQueriesShouldSupportSubTask() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        assertTrue("应定义日汇总/下游表切换与单编码过滤片段",
                content.contains("id=\"statNumDayOrSubSceneTable\"")
                        && content.contains("id=\"statWarningTypeIdFilter\"")
                        && content.contains("vm_statistics_event_sub_scene"));
        String detail = extractSelectBlock(content, "getEventDetailByType");
        assertTrue("详情应按 SUB_TASK 切到下游汇总表",
                detail.contains("SUB_TASK") && detail.contains("vm_statistics_event_sub_scene"));
        assertTrue("详情 SUB_TASK 应按 sub_scene_code 过滤",
                detail.contains("sub_scene_code"));
        String trend = extractSelectBlock(content, "getEventNumListApp");
        assertTrue("折线应 include 下游日汇总表片段",
                trend.contains("statNumDayOrSubSceneTable")
                        && trend.contains("statWarningTypeIdFilter"));
        assertTrue("折线应对 SUB_TASK 使用下游表 orderStatus 映射",
                trend.contains("SUB_TASK")
                        && (trend.contains("statSubSceneOrderStatusNumItem")
                        || readUtf8(xml).contains("statSubSceneOrderStatusNumItem")));
    }

    /**
     * Spec：小时/压制已支持下游 — Service 不再对 SUB_TASK 短路；SQL 切下游汇总表。
     */
    @Test
    public void hourAndInterceptShouldQuerySubSceneTablesOnSubTask() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        assertTrue(extractSelectBlock(content, "getEventNumDetailByHour")
                .contains("statHourOrSubSceneTable")
                || extractSelectBlock(content, "getEventNumDetailByHour")
                .contains("vm_statistics_event_hour_sub_scene"));
        assertTrue(extractSelectBlock(content, "getEventDetailInterceptNumApp")
                .contains("statInterceptDayOrSubSceneTable")
                || extractSelectBlock(content, "getEventDetailInterceptNumApp")
                .contains("vm_statistics_intercept_event_sub_scene"));
        assertTrue(extractSelectBlock(content, "getEventNumDetailByInterceptHour")
                .contains("statHourInterceptOrSubSceneTable")
                || extractSelectBlock(content, "getEventNumDetailByInterceptHour")
                .contains("vm_statistics_event_hour_intercept_sub_scene"));
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/service/WorkbenchService.java");
        String svc = readUtf8(service);
        // 不应再对小时/压制做 SUB_TASK 空列表短路（保留 isSubTask 给其它逻辑亦可）
        int hourIdx = svc.indexOf("getEventNumDetailByHour");
        String hourMethod = svc.substring(hourIdx, svc.indexOf("getEventNumByInterceptHour", hourIdx));
        assertTrue("小时趋势不应短路 emptyList",
                !hourMethod.contains("Collections.emptyList()"));
    }

    /**
     * Spec：大屏告警类型排行 getDevTypeYearMonthAppScreen → getEventAlarmNumTypeIndustry
     * 须支持 statDimension=SUB_TASK 读下游日汇总。
     */
    @Test
    public void appScreenTypeRankShouldSupportSubTask() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String block = extractSelectBlock(readUtf8(xml), "getEventAlarmNumTypeIndustry");
        assertTrue("大屏类型排行应支持 SUB_TASK 切到下游日汇总",
                block.contains("SUB_TASK") && block.contains("vm_statistics_event_sub_scene"));
        assertTrue("SUB_TASK 应按 sub_scene_code 分组/出码",
                block.contains("sub_scene_code"));
        assertTrue("无 SUB_TASK 应保留原 num_day 表",
                block.contains("vm_statistics_event_num_day"));
        assertTrue("SUB_TASK 应 include 下游码过滤片段",
                block.contains("statSubSceneIdListFilter"));
        assertTrue("应定义 subSceneIdList→sub_scene_code 过滤片段",
                readUtf8(xml).contains("id=\"statSubSceneIdListFilter\"")
                        && readUtf8(xml).contains("subSceneIdList"));
    }

    /**
     * Spec：大屏全选工单状态/来源应归一为未筛选，使 SUB_TASK 与不传 orderStatusList 结果一致。
     */
    @Test
    public void appScreenShouldNormalizeFullStatusFilters() throws Exception {
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/service/WorkbenchService.java");
        Path util = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/util/StatDimensionConstants.java");
        String svc = readUtf8(service);
        String constants = readUtf8(util);
        assertTrue("应提供全选过滤归一方法",
                constants.contains("normalizeScreenAllFilters"));
        assertTrue("大屏排行入口应调用全选归一",
                svc.contains("normalizeScreenAllFilters(analysisVO)"));
        assertTrue("应识别大屏全选工单状态集合",
                constants.contains("ALL_ORDER_STATUS_CODES"));
        assertTrue("SUB_TASK 应声明可映射状态全集（含 2/4/5/9）",
                constants.contains("SUB_TASK_MAPPED_ORDER_STATUS")
                        && constants.contains("\"2\"")
                        && constants.contains("\"4\"")
                        && constants.contains("\"5\"")
                        && constants.contains("\"9\""));
        assertTrue("SUB_TASK 多选应保留可映射状态而非整表清空",
                constants.contains("retainSubTaskMappedOrderStatus"));
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        assertTrue("应定义 SUB_TASK orderStatus 累加/条件片段",
                content.contains("id=\"statSubSceneOrderStatusNumItem\"")
                        && content.contains("id=\"statSubSceneOrderStatusWhereItem\"")
                        && content.contains("dispatch_num")
                        && content.contains("acceptance_num"));
    }

    /**
     * Spec：告警来源统计 getEventAlarmNumSourceIndustry 兼容下游过滤。
     * 有 subSceneIdList 或 SUB_TASK 时读下游日汇总，仍按 warning_source 分组。
     */
    @Test
    public void sourceIndustryShouldSupportSubSceneFilter() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        String block = extractSelectBlock(content, "getEventAlarmNumSourceIndustry");
        assertTrue("来源统计应支持切到下游日汇总",
                block.contains("vm_statistics_event_sub_scene"));
        assertTrue("来源统计应支持 subSceneIdList 或 SUB_TASK 触发切表",
                block.contains("subSceneIdList") || block.contains("SUB_TASK"));
        assertTrue("切下游后仍应按 warning_source 分组",
                block.contains("warning_source") || block.contains("warningSource"));
        assertTrue("无下游应保留原 num_day 表",
                block.contains("vm_statistics_event_num_day"));
    }

    /**
     * Spec：APP TYPE 告警类型排行须展示大模型场景；
     * SUB_TASK 须 enrich 为「下游任务 + 小模型、剔场景」。
     */
    @Test
    public void appTypeRankShouldNotFilterSubSceneBySceneExcludedTypes() throws Exception {
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/service/WorkbenchService.java");
        String svc = readUtf8(service);
        int start = svc.indexOf("public List<Map> getDevTypeYearMonthApp");
        int end = svc.indexOf("public TableDataInfo getDevTypeYearMonthAppTop", start);
        assertTrue("应能定位 getDevTypeYearMonthApp", start >= 0 && end > start);
        String method = svc.substring(start, end);
        assertTrue("APP 排行 SUB_TASK 须并入小模型并剔场景",
                method.contains("enrichStatisticsSubTaskWithSmallModels"));
        assertTrue("应提供场景编码加载以剔除场景维度",
                svc.contains("loadSceneIdSet") && svc.contains("getSceneIds"));
        assertTrue("应提供下游白名单，避免与场景 type_value 重叠时误删",
                svc.contains("getDownstreamAlarmTypeIds") && svc.contains("keepSubTaskCustomerRankId"));
        assertTrue("应提供纯小模型类型列表",
                svc.contains("loadSmallModelAlarmTypeValues"));
    }

    /**
     * Spec：TYPE 告警类型准确率含场景；SUB_TASK 下游准确率 = 下游 + 小模型、不带场景。
     * 准确率公式仍为 (真警+重复)/(真警+重复+误报)。
     */
    @Test
    public void typeAccuracyShouldNotFilterSceneIds() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String content = readUtf8(xml);
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/service/WorkbenchService.java");
        String svc = readUtf8(service);
        int start = svc.indexOf("public TableDataInfo getEvenTypeAccuracy");
        int end = svc.indexOf("public List<Map> getEvenSourceAccuracy", start);
        assertTrue("应能定位 getEvenTypeAccuracy", start >= 0 && end > start);
        int annStart = svc.lastIndexOf("@DataScope", start);
        assertTrue("应能定位 getEvenTypeAccuracy 的 DataScope", annStart >= 0 && annStart < start);
        String method = svc.substring(annStart, end);
        assertTrue("准确率 SUB_TASK 须并入小模型并剔场景",
                method.contains("enrichAnalysisSubTaskWithSmallModels"));
        assertTrue("类型准确率 DataScope 不得套 typeValueColumn 过滤场景",
                !method.contains("typeValueColumn"));
        String accuracy = extractSelectBlock(content, "getEvenTypeAccuracy");
        assertTrue("准确率应为 (real+repeat)/(real+repeat+false)",
                accuracy.contains("real_num") && accuracy.contains("repeat_complete_num")
                        && accuracy.contains("false_num"));
    }

    /**
     * Spec：大屏/PC 下游任务排行入口须走 SUB_TASK 小模型并入。
     */
    @Test
    public void subTaskRankEntriesShouldEnrichSmallModels() throws Exception {
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/workbench/service/WorkbenchService.java");
        String svc = readUtf8(service);
        assertTrue("PC getDevTypeYearMonth 须 enrich SUB_TASK",
                extractMethod(svc, "public Object getDevTypeYearMonth",
                        "public List<Map> getDevTypeYearMonthApp")
                        .contains("enrichStatisticsSubTaskWithSmallModels"));
        assertTrue("大屏 Screen 须 enrich SUB_TASK",
                extractMethod(svc, "public TableDataInfo getDevTypeYearMonthAppScreen",
                        "public Result getEventNumListApp")
                        .contains("enrichAnalysisSubTaskWithSmallModels"));
        assertTrue("行业类型排行须 enrich SUB_TASK",
                extractMethod(svc, "public List<Map> getEventAlarmNumTypeIndustry",
                        "public Object getDevTypeYearMonth")
                        .contains("enrichAnalysisSubTaskWithSmallModels"));
        assertTrue("点进详情 SUB_TASK 无数据须回退 TYPE",
                extractMethod(svc, "public List<Map> getEventDetailByType",
                        "public List<Map> getEvenAreaCompletionPc")
                        .contains("StatDimensionConstants.TYPE"));
        assertTrue("折线 SUB_TASK 小模型须探测回退",
                extractMethod(svc, "public Result getEventNumListApp",
                        "public Result getEventNumLevelList")
                        .contains("needSubTaskSmallModelDrillDownFallback"));
    }

    /**
     * Spec：大屏 getEventTypeStatistics 在 SUB_TASK 须并入小模型并剔场景。
     */
    @Test
    public void bigScreenTypeStatisticsShouldEnrichSubTaskSmallModels() throws Exception {
        Path service = resolveUnder(
                "src/main/java/com/chinatower/analysis/modules/statistic/service/BigScreenService.java");
        String svc = readUtf8(service);
        assertTrue("大屏类型统计须识别 SUB_TASK",
                svc.contains("isSubTask") && svc.contains("getEventTypeStatistics"));
        assertTrue("大屏 SUB_TASK 须剔纯场景并保留下游白名单",
                svc.contains("filterPureSceneCodes") && svc.contains("getDownstreamAlarmTypeIds"));
        assertTrue("大屏 SUB_TASK 须并入纯小模型",
                svc.contains("filterSmallModelCodes") && svc.contains("mergeEventTypeStatisticRows"));
    }

    private static String extractMethod(String svc, String startToken, String endToken) {
        int start = svc.indexOf(startToken);
        int end = svc.indexOf(endToken, start);
        if (start < 0 || end <= start) {
            return "";
        }
        return svc.substring(start, end);
    }

    private static String extractSelectBlock(String content, String id) {
        String startToken = "id=\"" + id + "\"";
        int start = content.indexOf(startToken);
        if (start < 0) {
            return "";
        }
        int end = content.indexOf("</select>", start);
        if (end < 0) {
            return content.substring(start);
        }
        return content.substring(start, end);
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveUnder(String relative) {
        Path fromSibling = Paths.get("../video-analysis/" + relative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-services/video-analysis/" + relative);
        assertTrue("找不到: " + relative, Files.exists(fromRepo));
        return fromRepo;
    }

    private static Path resolveApi(String relative) {
        Path fromSibling = Paths.get("../../video-api/video-api-analysis/" + relative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-api/video-api-analysis/" + relative);
        assertTrue("找不到 API: " + relative, Files.exists(fromRepo));
        return fromRepo;
    }
}
