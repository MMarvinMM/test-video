package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Spec：服务铁塔的统计沉淀按子任务拆行（Task 6）。
 * 轻量文件契约：TowerStatisticsDAO 月表分支 JSON_TABLE 展开 sub_scenes；
 * 客户 StatisticsDAO.getEventTypeForTenant 保持场景口径不拆行。
 */
public class TowerStatisticsSubScenesXmlTest {

    /**
     * Spec 场景：多子任务拆行写入 / 空 sub_scenes 回退
     * WHEN 铁塔统计 Job getEventType 沉淀
     * THEN 月表 SQL 含 JSON_TABLE 与 sub_scenes 展开，并以 jt.sub_type 作为 warning_type_id
     */
    @Test
    public void towerGetEventTypeShouldExplodeSubScenesWithJsonTable() throws Exception {
        String getEventTypeBlock = extractSelectBlock(readUtf8(resolveTowerStatisticsDaoXml()), "getEventType");

        assertTrue("getEventType 应对 sub_scenes 做 JSON_TABLE 展开",
                getEventTypeBlock.contains("JSON_TABLE"));
        assertTrue("getEventType 应引用 sub_scenes",
                getEventTypeBlock.contains("sub_scenes"));
        assertTrue("空/NULL sub_scenes 应回退 JSON_ARRAY(warning_type_id)",
                getEventTypeBlock.contains("JSON_ARRAY")
                        && getEventTypeBlock.contains("warning_type_id"));
        assertTrue("展开后应按 jt.sub_type 作为 warning_type_id 聚合",
                getEventTypeBlock.contains("jt.sub_type")
                        || getEventTypeBlock.contains("AS warning_type_id"));
    }

    /**
     * Spec 场景：多子任务拆行写入（算法统计）
     * WHEN 铁塔统计 Job getEventAlgorithm 沉淀
     * THEN 月表与运营表分支均按 sub_scenes JSON_TABLE 拆行；intercept 仍用 warning_type_id
     */
    @Test
    public void towerGetEventAlgorithmMonthBranchShouldExplodeSubScenes() throws Exception {
        String getEventAlgorithmBlock = extractSelectBlock(
                readUtf8(resolveTowerStatisticsDaoXml()), "getEventAlgorithm");

        assertTrue("getEventAlgorithm 月表分支应对 sub_scenes 做 JSON_TABLE 展开",
                getEventAlgorithmBlock.contains("JSON_TABLE"));
        assertTrue("getEventAlgorithm 应引用 sub_scenes",
                getEventAlgorithmBlock.contains("sub_scenes"));
        assertTrue("getEventAlgorithm 运营表分支也应 JSON_TABLE 拆行",
                getEventAlgorithmBlock.contains("vm_alarm_operate_t")
                        && getEventAlgorithmBlock.indexOf("JSON_TABLE",
                        getEventAlgorithmBlock.indexOf("vm_alarm_operate_t") - 200) >= 0);
    }

    /**
     * Spec 场景：客户统计不拆分 / 客户统计回归
     * WHEN 客户视角 getEventTypeForTenant
     * THEN 不得引入 JSON_TABLE / sub_scenes 拆行
     */
    @Test
    public void customerGetEventTypeForTenantMustNotExplodeSubScenes() throws Exception {
        String block = extractSelectBlock(readUtf8(resolveStatisticsDaoXml()), "getEventTypeForTenant");

        assertFalse("客户 getEventTypeForTenant 不得使用 JSON_TABLE 拆行",
                block.contains("JSON_TABLE"));
        assertFalse("客户 getEventTypeForTenant 不得按 sub_scenes 展开",
                block.contains("sub_scenes"));
        assertTrue("客户口径仍按 warning_type_id 聚合",
                block.contains("aa.warning_type_id"));
    }

    /**
     * 铁塔运营研判算法统计 Job：直查月表应按 sub_scenes 拆行。
     */
    @Test
    public void statisticsAlarmGetAlarmNumListShouldExplodeSubScenes() throws Exception {
        String block = extractSelectBlock(readUtf8(resolveStatisticsAlarmDaoXml()), "getAlarmNumList");
        assertTrue("getAlarmNumList 应对 sub_scenes 做 JSON_TABLE 展开", block.contains("JSON_TABLE"));
        assertTrue("getAlarmNumList 应以 jt.sub_type 作为 warning_type_id",
                block.contains("jt.sub_type AS warning_type_id"));
    }

    @Test
    public void statisticsAlarmGetAlarmNumListParamShouldExplodeSubScenes() throws Exception {
        String block = extractSelectBlock(readUtf8(resolveStatisticsAlarmDaoXml()), "getAlarmNumListParam");
        assertTrue("getAlarmNumListParam 应对 sub_scenes 做 JSON_TABLE 展开", block.contains("JSON_TABLE"));
        assertTrue("getAlarmNumListParam 应引用 sub_scenes", block.contains("sub_scenes"));
        assertTrue("空 sub_scenes 应回退 JSON_ARRAY(warning_type_id)",
                block.contains("JSON_ARRAY(aa.warning_type_id)"));
    }

    private static String extractSelectBlock(String content, String selectId) {
        String marker = "id=\"" + selectId + "\"";
        int start = content.indexOf(marker);
        assertTrue("应存在 select id=" + selectId, start >= 0);
        // 回退到 <select
        int selectStart = content.lastIndexOf("<select", start);
        assertTrue("应定位到 <select " + selectId, selectStart >= 0);
        int end = content.indexOf("</select>", start);
        assertTrue("应存在 </select> for " + selectId, end >= 0);
        return content.substring(selectStart, end + "</select>".length());
    }

    private static String readUtf8(Path xml) throws Exception {
        return new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);
    }

    private static Path resolveTowerStatisticsDaoXml() {
        return resolveUnder("video-task",
                "src/main/resources/mapper/statistics/TowerStatisticsDAO.xml");
    }

    private static Path resolveStatisticsDaoXml() {
        return resolveUnder("video-task",
                "src/main/resources/mapper/statistics/StatisticsDAO.xml");
    }

    private static Path resolveStatisticsAlarmDaoXml() {
        return resolveUnder("video-task",
                "src/main/resources/mapper/statistics/StatisticsAlarmDAO.xml");
    }

    /**
     * 解析 video-services/{module}/{resourceRelative}，兼容从 receive 模块或仓库根运行。
     */
    private static Path resolveUnder(String module, String resourceRelative) {
        Path fromSibling = Paths.get("../" + module + "/" + resourceRelative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-services/" + module + "/" + resourceRelative);
        assertTrue("找不到 " + module + " 资源: " + fromRepo.toAbsolutePath(),
                Files.exists(fromRepo));
        return fromRepo;
    }
}
