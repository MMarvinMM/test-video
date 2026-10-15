package com.chinatower.platform.modules.statistics;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Spec：铁塔算法统计按 sub_scenes 拆行；空 sub_scenes 回退 warning_type_id。
 * 弥补 eventAlgorithmForTowerHandler 仅统计「昨天」、无法按入参刷历史的能力。
 */
public class EventAlgorithmHistoryForTowerContractTest {

    private static final String HANDLER = "eventAlgorithmHistoryForTowerHandler";
    private static final String GET_ID = "getEventAlgorithmByDate";
    private static final String DEL_ID = "delEventAlgorithmByDate";
    private static final String INSERT_ID = "insertEventAlgorithmByDate";

    /**
     * WHEN 注册历史刷数 XXL Job
     * THEN TowerStatisticsService 暴露 eventAlgorithmHistoryForTowerHandler，
     * AND 入参非空时按逗号日期循环，空结果仍先删后写
     */
    @Test
    public void towerServiceShouldExposeAlgorithmHistoryJobHandler() throws Exception {
        Path javaFile = resolveUnder(
                "src/main/java/com/chinatower/platform/modules/statistics/service/TowerStatisticsService.java");
        String content = readUtf8(javaFile);

        assertTrue("应注册 " + HANDLER, content.contains("@XxlJob(\"" + HANDLER + "\")"));
        assertTrue("应按入参拆分日期循环", content.contains("param.split(\",\")"));
        assertTrue("应调用按日/月查询",
                content.contains("getEventAlgorithmByDate")
                        || content.contains(GET_ID));
        assertTrue("空 list 仍应沉淀（先删后写）",
                content.contains("Collections.emptyList()")
                        && content.contains("insertEventAlgorithmByDate"));
        // 历史 Job 不得再写死昨天
        String handlerBlock = extractMethodBlock(content, "eventAlgorithmHistoryHandler");
        if (handlerBlock.isEmpty()) {
            handlerBlock = extractMethodAroundMarker(content, HANDLER);
        }
        assertFalse("历史 Job 不得 DATE_SUB(NOW(),1)",
                handlerBlock.contains("DATE_SUB(NOW()"));
    }

    /**
     * WHEN DAO 提供按 DateVO 的算法统计查询
     * THEN SQL 使用 handleDate 过滤（日/月），按 alarm_time 出 year/month/day，
     * AND 月表分支 JSON_TABLE(sub_scenes) 空值回退 warning_type_id
     */
    @Test
    public void towerDaoShouldQueryAlgorithmByDateWithSubScenesSplit() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/statistics/TowerStatisticsDAO.xml");
        String content = readUtf8(xml);
        String block = extractSelectBlock(content, GET_ID);

        assertTrue("应定义 " + GET_ID, content.contains("id=\"" + GET_ID + "\""));
        assertTrue("应按 sub_scenes JSON_TABLE 拆行",
                block.contains("JSON_TABLE") && block.contains("sub_scenes"));
        assertTrue("空 sub_scenes 应回退 JSON_ARRAY(warning_type_id)",
                block.contains("JSON_ARRAY(t.warning_type_id)")
                        || block.contains("JSON_ARRAY(aa.warning_type_id)"));
        assertTrue("应按入参 handleDate 过滤，而非写死昨天",
                block.contains("#{handleDate}")
                        && !block.contains("DATE_SUB(NOW(),INTERVAL 1 day)"));
        assertTrue("year/month/day 应来自 alarm_time",
                block.contains("YEAR(") && block.contains("MONTH(") && block.contains("DAY("));
        assertTrue("应按天分组，支持按月入参拆到日",
                block.contains("DATE_FORMAT(a.alarm_time")
                        || block.contains("DATE_FORMAT(a.alarm_time,"));
    }

    /**
     * WHEN 历史重跑
     * THEN 删除按 year/month[/day]，插入 create_date 使用统计日而非 NOW()-1
     */
    @Test
    public void towerDaoShouldDeleteAndInsertAlgorithmByDate() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/statistics/TowerStatisticsDAO.xml");
        String content = readUtf8(xml);

        assertTrue("应定义 " + DEL_ID, content.contains("id=\"" + DEL_ID + "\""));
        assertTrue("应定义 " + INSERT_ID, content.contains("id=\"" + INSERT_ID + "\""));

        String delBlock = extractTagBlock(content, "delete", DEL_ID);
        assertTrue("删除应按 year/month",
                delBlock.contains("#{year}") && delBlock.contains("#{month}"));
        assertTrue("删除支持按 day",
                delBlock.contains("#{day}") || delBlock.contains("day"));

        String insertBlock = extractTagBlock(content, "insert", INSERT_ID);
        assertFalse("插入 create_date 不得写死 DATE_SUB(NOW(),1)",
                insertBlock.contains("DATE_SUB(NOW(),INTERVAL 1 day)"));
        assertTrue("插入应写入 year/month/day",
                insertBlock.contains("year_str") || insertBlock.contains("`year`"));
    }

    /**
     * WHEN 声明 DAO 接口
     * THEN TowerStatisticsDAO 暴露按 DateVO 的 get/del/insert
     */
    @Test
    public void towerDaoInterfaceShouldDeclareByDateMethods() throws Exception {
        Path javaFile = resolveUnder(
                "src/main/java/com/chinatower/platform/modules/statistics/dao/TowerStatisticsDAO.java");
        String content = readUtf8(javaFile);
        assertTrue(content.contains(GET_ID));
        assertTrue(content.contains(DEL_ID));
        assertTrue(content.contains(INSERT_ID));
    }

    private static String extractSelectBlock(String xml, String selectId) {
        return extractTagBlock(xml, "select", selectId);
    }

    private static String extractTagBlock(String xml, String tag, String id) {
        String marker = "id=\"" + id + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 " + tag + ": " + id, start >= 0);
        String endTag = "</" + tag + ">";
        int end = xml.indexOf(endTag, start);
        assertTrue("找不到 " + endTag + ": " + id, end > start);
        return xml.substring(start, end);
    }

    private static String extractMethodBlock(String java, String methodName) {
        String marker = " " + methodName + "(";
        int start = java.indexOf(marker);
        if (start < 0) {
            return "";
        }
        int brace = java.indexOf('{', start);
        if (brace < 0) {
            return "";
        }
        int depth = 0;
        for (int i = brace; i < java.length(); i++) {
            char c = java.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return java.substring(start, i + 1);
                }
            }
        }
        return java.substring(start);
    }

    private static String extractMethodAroundMarker(String java, String marker) {
        int start = java.indexOf(marker);
        if (start < 0) {
            return "";
        }
        int brace = java.indexOf('{', start);
        if (brace < 0) {
            return java.substring(Math.max(0, start - 80), Math.min(java.length(), start + 400));
        }
        int depth = 0;
        for (int i = brace; i < java.length(); i++) {
            char c = java.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return java.substring(start, i + 1);
                }
            }
        }
        return java.substring(start);
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
}
