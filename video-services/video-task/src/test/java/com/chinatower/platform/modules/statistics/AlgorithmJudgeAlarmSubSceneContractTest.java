package com.chinatower.platform.modules.statistics;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spec：铁塔算法统计列表告警类型 — 大模型按下游任务维度，不得按场景维度。
 */
public class AlgorithmJudgeAlarmSubSceneContractTest {

    /**
     * WHEN 运营研判 getOperateNumList / Param 统计
     * THEN 优先运营表 aa.sub_scenes，空则 JOIN 月表，仍空回退 warning_type_id
     */
    @Test
    public void operateNumListShouldExplodeSubScenesViaMonthJoin() throws Exception {
        String xml = readUtf8(resolve("src/main/resources/mapper/statistics/StatisticsAlarmDAO.xml"));
        for (String id : new String[]{"getOperateNumList", "getOperateNumListParam"}) {
            String block = extractSelectBlock(xml, id);
            Assert.assertTrue(id + " 应优先读运营表 aa.sub_scenes",
                    block.contains("aa.sub_scenes"));
            Assert.assertTrue(id + " 应 JOIN 月表取 sub_scenes 作次选",
                    block.contains("${tableName}") && block.contains("ord.sub_scenes"));
            Assert.assertTrue(id + " 应按 JSON_TABLE 拆行",
                    block.contains("JSON_TABLE") && block.contains("jt.sub_type"));
            Assert.assertTrue(id + " JOIN 月表后 warning_source 须带 aa. 前缀（避免歧义导致本级统计失败）",
                    block.contains("aa.warning_source IN"));
            Assert.assertFalse(id + " 不得使用无前缀 warning_source IN",
                    block.contains("and warning_source IN")
                            || block.contains("\n            and warning_source IN"));
        }
    }

    /**
     * WHEN 沉淀算法统计
     * THEN Service 剔除纯场景，但保留 scene_alarm_rel 下游编码（可与场景 type_value 重叠）；Job 空结果仍先删后写
     */
    @Test
    public void algorithmJudgePersistShouldExcludeScenesAndAlwaysWrite() throws Exception {
        String dao = readUtf8(resolve("src/main/resources/mapper/statistics/StatisticsDAO.xml"));
        int sceneIdx = dao.indexOf("id=\"getSceneIds\"");
        Assert.assertTrue(sceneIdx >= 0);
        String sceneBlock = dao.substring(sceneIdx, dao.indexOf("</select>", sceneIdx));
        Assert.assertTrue("剔除场景须含 type_value",
                sceneBlock.contains("type_value"));
        Assert.assertTrue("剔除场景须兼容 scene_id（含去横线）",
                sceneBlock.contains("scene_id") && sceneBlock.contains("REPLACE"));
        Assert.assertTrue("须定义下游编码白名单 getDownstreamAlarmTypeIds",
                dao.contains("id=\"getDownstreamAlarmTypeIds\"")
                        && dao.contains("sys_scene_alarm_rel"));
        Assert.assertTrue("有参删除不得再按 colony 过滤（否则下级属地场景残留）",
                !extractDeleteBlock(readUtf8(resolve(
                        "src/main/resources/mapper/statistics/StatisticsAlarmDAO.xml")),
                        "delJudgeAlarmParam").contains("colony_code"));

        String service = readUtf8(resolve(
                "src/main/java/com/chinatower/platform/modules/statistics/service/impl/StatisticsAlarmServiceImpl.java"));
        Assert.assertTrue("汇总后应对 JudgeVO 再剔场景",
                service.contains("excludeSceneJudgeVos"));
        Assert.assertTrue("应调用 getSceneIds 剔除场景",
                service.contains("getSceneIds"));
        Assert.assertTrue("剔除时须保留下游任务编码（避免双重身份误删）",
                service.contains("getDownstreamAlarmTypeIds")
                        && service.contains("keepWarningTypeId"));
        Assert.assertTrue("应填充下游任务名称",
                service.contains("getSubSceneAlarmTypeNames")
                        || service.contains("warningTypeName"));

        String task = readUtf8(resolve(
                "src/main/java/com/chinatower/platform/modules/statistics/task/StatisticsAlarmTask.java"));
        Assert.assertTrue("应调用 insertEventJudgeAlarm",
                task.contains("insertEventJudgeAlarm"));
        Assert.assertTrue("应调用 insertEventJudgeAlarmParam",
                task.contains("insertEventJudgeAlarmParam"));
        // 不得再仅在非空时沉淀（会残留历史场景脏数据）
        Assert.assertFalse("空 list 不得跳过 insertEventJudgeAlarm",
                task.contains("if (!CollectionUtils.isEmpty(list)) {\n"
                        + "                statisticsAlarmService.insertEventJudgeAlarm"));
        Assert.assertFalse("空 list 不得跳过 insertEventJudgeAlarmParam",
                task.contains("if (!CollectionUtils.isEmpty(list)) {\n"
                        + "                    statisticsAlarmService.insertEventJudgeAlarmParam"));
    }

    /**
     * WHEN 铁塔 getEventAlgorithm 沉淀
     * THEN Service 同样剔除场景编码
     */
    @Test
    public void towerEventAlgorithmShouldExcludeSceneIds() throws Exception {
        String java = readUtf8(resolve(
                "src/main/java/com/chinatower/platform/modules/statistics/service/TowerStatisticsService.java"));
        String getAlgo = extractMethod(java, "private List<Map> getEventAlgorithm");
        String getAlgoByDate = extractMethod(java, "private List<Map> getEventAlgorithmByDate");
        Assert.assertTrue("getEventAlgorithm 应剔除场景",
                getAlgo.contains("getSceneIds") || getAlgo.contains("loadSceneIdSet")
                        || getAlgo.contains("filterScene"));
        Assert.assertTrue("getEventAlgorithmByDate 应剔除场景",
                getAlgoByDate.contains("getSceneIds") || getAlgoByDate.contains("loadSceneIdSet")
                        || getAlgoByDate.contains("filterScene"));
    }

    private static String extractMethod(String java, String signaturePrefix) {
        int start = java.indexOf(signaturePrefix);
        Assert.assertTrue("找不到方法: " + signaturePrefix, start >= 0);
        int brace = java.indexOf('{', start);
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

    private static String extractSelectBlock(String xml, String id) {
        String marker = "id=\"" + id + "\"";
        int start = xml.indexOf(marker);
        Assert.assertTrue("找不到 select " + id, start >= 0);
        int end = xml.indexOf("</select>", start);
        Assert.assertTrue(end > start);
        return xml.substring(start, end);
    }

    private static String extractDeleteBlock(String xml, String id) {
        String marker = "id=\"" + id + "\"";
        int start = xml.indexOf(marker);
        Assert.assertTrue("找不到 delete " + id, start >= 0);
        int end = xml.indexOf("</delete>", start);
        Assert.assertTrue(end > start);
        return xml.substring(start, end);
    }

    private static String readUtf8(Path path) throws Exception {
        Assert.assertTrue("找不到: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolve(String relative) {
        Path fromModule = Paths.get(relative);
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        return Paths.get("video-services/video-task").resolve(relative);
    }
}
