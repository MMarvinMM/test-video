package com.chinatower.platform.modules.statistics;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spec：铁塔 *_tt 告警类型统计沉淀后，大模型拆行编码应展示下游任务名称，而非仅场景名。
 */
public class TowerEventTypeNameContractTest {

    /**
     * WHEN 铁塔 getEventType 写 warning_type_name
     * THEN 名称字典须包含 sys_scene_alarm_rel，且 Service 优先用下游名覆盖
     */
    @Test
    public void towerEventTypeShouldResolveDownstreamTaskNames() throws Exception {
        Path daoXml = resolve("src/main/resources/mapper/statistics/StatisticsDAO.xml");
        Assert.assertTrue(Files.exists(daoXml));
        String dao = new String(Files.readAllBytes(daoXml), StandardCharsets.UTF_8);
        Assert.assertTrue("应定义 getSubSceneAlarmTypeNames",
                dao.contains("id=\"getSubSceneAlarmTypeNames\""));
        Assert.assertTrue("下游名称应查 sys_scene_alarm_rel.alarm_type_name",
                dao.contains("sys_scene_alarm_rel") && dao.contains("alarm_type_name"));

        Path service = resolve(
                "src/main/java/com/chinatower/platform/modules/statistics/service/TowerStatisticsService.java");
        Assert.assertTrue(Files.exists(service));
        String java = new String(Files.readAllBytes(service), StandardCharsets.UTF_8);
        Assert.assertTrue("getEventType 应构建含下游名的字典",
                java.contains("buildTowerWarningTypeNameMap"));
        Assert.assertTrue("应调用 getSubSceneAlarmTypeNames",
                java.contains("getSubSceneAlarmTypeNames"));
    }

    /**
     * Spec：铁塔告警类型准确率/排行不得展示大模型场景，只展示下游任务。
     * WHEN 空 sub_scenes 回退 warning_type_id 且该编码为场景
     * THEN Service 须剔除 sys_scene_config 场景编码；Job 空结果仍先删后写
     */
    @Test
    public void towerEventTypeShouldExcludeSceneIdsAndAlwaysPersist() throws Exception {
        Path daoXml = resolve("src/main/resources/mapper/statistics/StatisticsDAO.xml");
        String dao = new String(Files.readAllBytes(daoXml), StandardCharsets.UTF_8);
        Assert.assertTrue("应定义 getSceneIds 查询场景编码",
                dao.contains("id=\"getSceneIds\"") && dao.contains("sys_scene_config"));
        int sceneIdx = dao.indexOf("id=\"getSceneIds\"");
        Assert.assertTrue(sceneIdx >= 0);
        String sceneBlock = dao.substring(sceneIdx, dao.indexOf("</select>", sceneIdx));
        Assert.assertTrue("getSceneIds 须查 type_value（与 warning_type_id 对齐）",
                sceneBlock.contains("type_value"));
        Assert.assertTrue("getSceneIds 须兼容 scene_id（含去横线）",
                sceneBlock.contains("scene_id") && sceneBlock.contains("REPLACE"));

        Path service = resolve(
                "src/main/java/com/chinatower/platform/modules/statistics/service/TowerStatisticsService.java");
        String java = new String(Files.readAllBytes(service), StandardCharsets.UTF_8);
        Assert.assertTrue("getEventType 应调用 getSceneIds 剔除场景",
                java.contains("getSceneIds"));
        Assert.assertTrue("剔除时须保留下游任务编码（避免双重身份误删）",
                java.contains("getDownstreamAlarmTypeIds")
                        && java.contains("keepWarningTypeId"));
        int handlerIdx = java.indexOf("eventTypeForTowerHandler");
        Assert.assertTrue(handlerIdx >= 0);
        String handlerBlock = java.substring(handlerIdx,
                java.indexOf("private List<Map> getEventType", handlerIdx));
        Assert.assertTrue("eventTypeForTowerHandler 应调用 insertEventType",
                handlerBlock.contains("insertEventType"));
        Assert.assertFalse("空 list 不得跳过 del+insert（否则残留场景脏数据）",
                handlerBlock.contains("CollectionUtils.isEmpty(list)"));
    }

    private static Path resolve(String relative) {
        Path fromModule = Paths.get(relative);
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        return Paths.get("video-services/video-task").resolve(relative);
    }
}
