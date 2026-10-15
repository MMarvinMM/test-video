package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Spec：领导批示列表支持下游任务筛选。
 * vm_order_leader_audit_t 无 sub_scenes，须 EXISTS 关联工单月表过滤；
 * LeaderAuidtListDTO 必须声明 subSceneIdList，避免 MyBatis 反射失败。
 */
public class LeaderAuditSubScenesJoinContractTest {

    private static final List<String> LEADER_AUDIT_MAPPERS = Arrays.asList(
            "video-services/video-event/src/main/resources/com/chinatower/event/mapper/WarningOrderMapper.xml",
            "video-services/video-export/src/main/resources/com/chinatower/platform/mapper/IWarningOrderMapper.xml"
    );

    /**
     * WHEN 领导批示 queryLeaderAuditList 需要下游任务过滤
     * THEN 不得直接 JSON_CONTAINS(dataTable.sub_scenes)；应 include EXISTS 月表片段
     */
    @Test
    public void queryLeaderAuditListShouldJoinMonthTableForSubScenes() throws Exception {
        for (String relative : LEADER_AUDIT_MAPPERS) {
            String content = readUtf8(resolve(relative));
            int queryIdx = content.indexOf("id=\"queryLeaderAuditList\"");
            assertTrue(relative + " 应定义 queryLeaderAuditList", queryIdx > 0);
            int nextSql = content.indexOf("<sql id=", queryIdx + 10);
            int nextSelect = content.indexOf("<select id=", queryIdx + 10);
            int end = content.length();
            if (nextSql > 0) {
                end = Math.min(end, nextSql);
            }
            if (nextSelect > 0) {
                end = Math.min(end, nextSelect);
            }
            String fragment = content.substring(queryIdx, end);

            assertFalse(relative + " 领导批示不得直接对 dataTable.sub_scenes 做 JSON_CONTAINS",
                    fragment.contains("dataTable.sub_scenes")
                            || fragment.contains("${alias}.sub_scenes"));
            assertTrue(relative + " 应 include subScenesOnlyFilterByOrderTables",
                    fragment.contains("refid=\"subScenesOnlyFilterByOrderTables\"")
                            || fragment.contains("subScenesOnlyFilterByOrderTables"));
            assertTrue(relative + " 应定义 EXISTS 月表片段（含 warningOrderTableList）",
                    content.contains("id=\"subScenesOnlyFilterByOrderTables\"")
                            && content.contains("warningOrderTableList")
                            && content.contains("EXISTS"));
            assertTrue(relative + " 应保留 warningTypeIdFilter（告警类型 ∧ 下游任务）",
                    fragment.contains("warningTypeIdFilter"));
        }
    }

    /**
     * WHEN 入参绑定 LeaderAuidtListDTO
     * THEN 必须声明 subSceneIdList 与 warningOrderTableList
     */
    @Test
    public void leaderAuditDtoShouldDeclareSubSceneAndTableList() throws Exception {
        String content = readUtf8(resolve(
                "video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/LeaderAuidtListDTO.java"));
        assertTrue("LeaderAuidtListDTO 应声明 subSceneIdList", content.contains("subSceneIdList"));
        assertTrue("LeaderAuidtListDTO 应声明 warningOrderTableList", content.contains("warningOrderTableList"));
    }

    /**
     * WHEN SearchOrderDTO / AlarmOrderParmaDto 的 SQL include 了 subScenesOnlyFilter
     * THEN DTO 必须有 subSceneIdList，否则空筛也会 ReflectionException
     */
    @Test
    public void otherDtosUsingSubScenesFilterShouldDeclareField() throws Exception {
        assertTrue("SearchOrderDTO 应声明 subSceneIdList",
                readUtf8(resolve(
                        "video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/SearchOrderDTO.java"))
                        .contains("subSceneIdList"));
        assertTrue("AlarmOrderParmaDto 应声明 subSceneIdList",
                readUtf8(resolve(
                        "video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/AlarmOrderParmaDto.java"))
                        .contains("subSceneIdList"));
        assertTrue("BigScreenOrderListDTO 应声明 subSceneIdList",
                readUtf8(resolve(
                        "video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/BigScreenOrderListDTO.java"))
                        .contains("subSceneIdList"));
        assertTrue("OrderListDTO 应声明 warningOrderTableList（getTodoNumLeader）",
                readUtf8(resolve(
                        "video-services/video-event/src/main/java/com/chinatower/event/dto/OrderListDTO.java"))
                        .contains("warningOrderTableList"));
    }

    private static String readUtf8(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolve(String repoRelative) {
        Path fromRepo = Paths.get(repoRelative);
        if (Files.exists(fromRepo)) {
            return fromRepo;
        }
        Path fromReceive = Paths.get("../..", repoRelative);
        if (Files.exists(fromReceive)) {
            return fromReceive;
        }
        Path fromModule = Paths.get("../../..", repoRelative);
        assertTrue("找不到文件: " + repoRelative, Files.exists(fromModule));
        return fromModule;
    }
}
