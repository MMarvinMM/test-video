package com.chinatower.platform.modules.statistics;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec：下游维度小时/压制沉淀 + 读侧 SUB_TASK。
 */
public class SubSceneHourInterceptContractTest {

    @Test
    public void ddlShouldDefineHourAndInterceptSubSceneTables() throws Exception {
        assertTrue(readUtf8(resolveSql("vm_statistics_event_hour_sub_scene.sql"))
                .contains("vm_statistics_event_hour_sub_scene"));
        assertTrue(readUtf8(resolveSql("vm_statistics_intercept_event_sub_scene.sql"))
                .contains("vm_statistics_intercept_event_sub_scene"));
        assertTrue(readUtf8(resolveSql("vm_statistics_event_hour_intercept_sub_scene.sql"))
                .contains("vm_statistics_event_hour_intercept_sub_scene"));
    }

    @Test
    public void statisticsDaoShouldAggregateHourBySubScenes() throws Exception {
        String xml = readUtf8(resolveUnder("src/main/resources/mapper/statistics/StatisticsDAO.xml"));
        String block = extractSelectBlock(xml, "getEventHourSubSceneForTenant");
        assertTrue(block.contains("JSON_TABLE") && block.contains("sub_scenes"));
        assertTrue(!block.contains("JSON_ARRAY(aa.warning_type_id)")
                && !block.contains("JSON_ARRAY(m.warning_type_id)"));
        assertTrue(xml.contains("vm_statistics_event_hour_sub_scene"));
        assertTrue(xml.contains("id=\"delEventHourSubSceneTenant\"")
                || xml.contains("id=\"deleteEventHourSubSceneTenant\""));
        assertTrue(xml.contains("id=\"addEventHourSubSceneTenant\"")
                || xml.contains("id=\"insertEventHourSubSceneTenant\""));
    }

    @Test
    public void statisticsDaoShouldAggregateInterceptBySubScenesViaOrderJoin() throws Exception {
        String xml = readUtf8(resolveUnder("src/main/resources/mapper/statistics/StatisticsDAO.xml"));
        String day = extractSelectBlock(xml, "getInterceptSubSceneForTenant");
        String hour = extractSelectBlock(xml, "getInterceptHourSubSceneForTenant");
        assertTrue("压制日应 JOIN 月表取 sub_scenes",
                day.contains("sub_scenes") && day.contains("JSON_TABLE"));
        assertTrue("压制小时应 JOIN 月表取 sub_scenes",
                hour.contains("sub_scenes") && hour.contains("JSON_TABLE"));
        assertTrue(xml.contains("vm_statistics_intercept_event_sub_scene"));
        assertTrue(xml.contains("vm_statistics_event_hour_intercept_sub_scene"));
    }

    @Test
    public void statisticsServiceShouldExposeHourAndInterceptSubSceneJobs() throws Exception {
        String content = readUtf8(resolveUnder(
                "src/main/java/com/chinatower/platform/modules/statistics/service/StatisticsService.java"));
        assertTrue(content.contains("eventHourSubSceneForTenantHandler"));
        assertTrue(content.contains("interceptSubSceneForTenantHandler"));
        assertTrue(content.contains("interceptHourSubSceneForTenantHandler"));
        assertTrue(content.contains("Collections.emptyList()")
                && content.contains("addEventHourSubSceneTenant"));
    }

    @Test
    public void workbenchQueriesShouldSupportSubTaskHourAndIntercept() throws Exception {
        Path xml = Paths.get("../video-analysis/src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        if (!Files.exists(xml)) {
            xml = Paths.get("video-services/video-analysis/src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        }
        String content = readUtf8(xml);
        assertTrue("应定义小时/压制表切换片段",
                content.contains("statHourOrSubSceneTable")
                        && content.contains("statHourInterceptOrSubSceneTable")
                        && content.contains("statInterceptDayOrSubSceneTable")
                        && content.contains("vm_statistics_event_hour_sub_scene")
                        && content.contains("vm_statistics_intercept_event_sub_scene")
                        && content.contains("vm_statistics_event_hour_intercept_sub_scene"));
        assertTrue(extractSelectBlock(content, "getEventNumDetailByHour")
                .contains("statHourOrSubSceneTable"));
        assertTrue(extractSelectBlock(content, "getEventDetailInterceptNumApp")
                .contains("statInterceptDayOrSubSceneTable"));
        assertTrue(extractSelectBlock(content, "getEventNumDetailByInterceptHour")
                .contains("statHourInterceptOrSubSceneTable"));
    }

    private static String extractSelectBlock(String xml, String selectId) {
        String marker = "id=\"" + selectId + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 select: " + selectId, start >= 0);
        int end = xml.indexOf("</select>", start);
        assertTrue("找不到 </select>: " + selectId, end > start);
        return xml.substring(start, end);
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveUnder(String relative) {
        Path fromModule = Paths.get(relative);
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        Path fromRepo = Paths.get("video-services/video-task/" + relative);
        assertTrue("找不到: " + relative, Files.exists(fromRepo));
        return fromRepo;
    }

    private static Path resolveSql(String fileName) {
        Path p = Paths.get("openspec/changes/llm-subtask-hour-intercept/sql/" + fileName);
        if (Files.exists(p)) {
            return p;
        }
        p = Paths.get("../../openspec/changes/llm-subtask-hour-intercept/sql/" + fileName);
        assertTrue("找不到 DDL: " + fileName, Files.exists(p));
        return p;
    }
}
