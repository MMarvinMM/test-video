package com.chinatower.platform.modules.statistics;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec V2.0 方案 A：客户下游任务维度汇总表 + Job 按 sub_scenes 拆行写入。
 */
public class SubSceneStatisticsContractTest {

    /**
     * WHEN 落地方案 A 汇总表
     * THEN DDL 包含 vm_statistics_event_sub_scene 与 sub_scene_code
     */
    @Test
    public void ddlShouldDefineSubSceneStatisticsTable() throws Exception {
        Path ddl = resolveOpenspecSql("vm_statistics_event_sub_scene.sql");
        String content = readUtf8(ddl);
        assertTrue(content.contains("vm_statistics_event_sub_scene"));
        assertTrue(content.contains("sub_scene_code"));
        assertTrue(content.contains("warning_num") || content.contains("ai_num"));
        // 方案 A：组合过滤需要类型×下游，日汇总须落 warning_type_id
        assertTrue("下游日汇总 DDL 应含 warning_type_id",
                content.contains("warning_type_id"));
        assertTrue("下游日汇总 DDL 应含派单/处置/审核/验收等环节列",
                content.contains("dispatch_num")
                        && content.contains("handle_num")
                        && content.contains("approve_num")
                        && content.contains("acceptance_num")
                        && content.contains("order_complete_num")
                        && content.contains("dispatch_real_num"));
    }

    /**
     * WHEN 沉淀下游日汇总（支持场景∧下游）
     * THEN SELECT/GROUP BY/INSERT 均带 warning_type_id（不回退空 sub_scenes）
     */
    @Test
    public void statisticsDaoSubSceneShouldPersistWarningTypeId() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/statistics/StatisticsDAO.xml");
        String content = readUtf8(xml);
        String select = extractSelectBlock(content, "getEventSubSceneForTenant");
        String insert = extractInsertBlock(content, "addEventSubSceneTenant");

        assertTrue("汇聚 SELECT 应选出 warning_type_id",
                select.contains("aa.warning_type_id") || select.contains("warning_type_id"));
        assertTrue("GROUP BY 应含 warning_type_id",
                select.contains("warning_type_id"));
        assertTrue("INSERT 应写入 warning_type_id",
                insert.contains("warning_type_id"));
        assertTrue("汇聚 SELECT 应统计派单/处置/审核/验收",
                select.contains("dispatch_num")
                        && select.contains("handle_num")
                        && select.contains("approve_num")
                        && select.contains("acceptance_num")
                        && select.contains("order_complete_num"));
        assertTrue("INSERT 应写入环节列",
                insert.contains("dispatch_num")
                        && insert.contains("handle_num")
                        && insert.contains("approve_num")
                        && insert.contains("acceptance_num")
                        && insert.contains("order_complete_real_num"));
        assertTrue("空 sub_scenes 仍不得回退 JSON_ARRAY(warning_type_id)",
                !select.contains("JSON_ARRAY(aa.warning_type_id)"));
    }

    /**
     * WHEN StatisticsDAO 沉淀下游任务维度
     * THEN 查询按 JSON_TABLE(sub_scenes) 拆行，写入独立汇总表
     */
    @Test
    public void statisticsDaoShouldSplitSubScenesIntoDedicatedTable() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/statistics/StatisticsDAO.xml");
        String content = readUtf8(xml);
        String block = extractSelectBlock(content, "getEventSubSceneForTenant");

        assertTrue("应定义 getEventSubSceneForTenant",
                content.contains("id=\"getEventSubSceneForTenant\""));
        assertTrue("应按 sub_scenes JSON_TABLE 拆行",
                block.contains("JSON_TABLE") && block.contains("sub_scenes"));
        assertTrue("应写入 vm_statistics_event_sub_scene",
                content.contains("vm_statistics_event_sub_scene"));
        assertTrue("应提供删除重跑",
                content.contains("id=\"delEventSubSceneTenant\"")
                        || content.contains("id=\"deleteEventSubSceneTenant\""));
        // 方案1：仅真实下游；空 sub_scenes 不回退 warning_type_id
        assertTrue("空 sub_scenes 不得回退 JSON_ARRAY(warning_type_id)",
                !block.contains("JSON_ARRAY(aa.warning_type_id)"));
        assertTrue("应过滤非空 sub_scenes",
                block.contains("JSON_LENGTH(aa.sub_scenes)")
                        || block.contains("JSON_LENGTH( sub_scenes )"));
    }

    /** 截取指定 select id 代码块，避免误匹配其它 SQL */
    private static String extractSelectBlock(String xml, String selectId) {
        String marker = "id=\"" + selectId + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 select: " + selectId, start >= 0);
        int end = xml.indexOf("</select>", start);
        assertTrue("找不到 </select>: " + selectId, end > start);
        return xml.substring(start, end);
    }

    /** 截取指定 insert id 代码块 */
    private static String extractInsertBlock(String xml, String insertId) {
        String marker = "id=\"" + insertId + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 insert: " + insertId, start >= 0);
        int end = xml.indexOf("</insert>", start);
        assertTrue("找不到 </insert>: " + insertId, end > start);
        return xml.substring(start, end);
    }

    /**
     * WHEN 注册 XXL Job
     * THEN StatisticsService 暴露 eventSubSceneForTenantHandler，且查询为空仍执行沉淀（先删后写）
     */
    @Test
    public void statisticsServiceShouldExposeSubSceneJobHandler() throws Exception {
        Path javaFile = resolveUnder(
                "src/main/java/com/chinatower/platform/modules/statistics/service/StatisticsService.java");
        String content = readUtf8(javaFile);
        assertTrue(content.contains("eventSubSceneForTenantHandler"));
        assertTrue(content.contains("getEventSubSceneForTenant")
                || content.contains("addEventSubSceneTenant"));
        // 方案1：空结果也要 del，避免历史回退脏数据残留
        assertTrue("空 list 仍应调用 addEventSubSceneTenant",
                content.contains("Collections.emptyList()")
                        && content.contains("addEventSubSceneTenant"));
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到文件: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveUnder(String resourceRelative) {
        Path fromModule = Paths.get(resourceRelative);
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        Path fromRepo = Paths.get("video-services/video-task/" + resourceRelative);
        assertTrue("找不到资源: " + resourceRelative, Files.exists(fromRepo));
        return fromRepo;
    }

    private static Path resolveOpenspecSql(String fileName) {
        Path fromRepo = Paths.get(
                "openspec/changes/llm-subtask-alarm-query/sql/" + fileName);
        if (Files.exists(fromRepo)) {
            return fromRepo;
        }
        Path fromModule = Paths.get(
                "../../openspec/changes/llm-subtask-alarm-query/sql/" + fileName);
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        Path fromReceiveSibling = Paths.get(
                "../../../openspec/changes/llm-subtask-alarm-query/sql/" + fileName);
        assertTrue("找不到 DDL: " + fileName, Files.exists(fromReceiveSibling));
        return fromReceiveSibling;
    }
}
