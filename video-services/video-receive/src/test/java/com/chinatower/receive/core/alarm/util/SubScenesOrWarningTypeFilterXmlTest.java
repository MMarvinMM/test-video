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
 * Spec V2.0：客户视角告警类型 ∧ 下游任务组合过滤。
 * warningTypeIdList → 仅 warning_type_id；subSceneIdList → 仅 sub_scenes；二者 AND。
 */
public class SubScenesOrWarningTypeFilterXmlTest {

    private static final String EVENT_WARNING_TYPE_REF =
            "com.chinatower.event.mapper.WarningOrderMapper.warningTypeIdFilter";
    private static final String EVENT_SUB_SCENES_REF =
            "com.chinatower.event.mapper.WarningOrderMapper.subScenesOnlyFilter";

    private static final List<String> EVENT_REUSE_MAPPERS = Arrays.asList(
            "com/chinatower/event/mapper/AlarmOrderAppMapper.xml",
            "com/chinatower/event/mapper/WarningOrderCollectionMapper.xml",
            "com/chinatower/event/mapper/WarningOrderForTradeMapper.xml",
            "com/chinatower/event/mapper/WarningInterceptMapper.xml"
    );

    private static final List<String> EXPORT_REUSE_MAPPERS = Arrays.asList(
            "com/chinatower/platform/mapper/IWarningOrderMapper.xml",
            "com/chinatower/platform/mapper/IWarningOrderCollectionMapper.xml"
    );

    /**
     * Spec：未选下游任务时类型利旧 / 下游任务仅 sub_scenes
     * WHEN WarningOrderMapper 定义过滤片段
     * THEN 存在 warningTypeIdFilter 与 subScenesOnlyFilter；不得再把二者 OR 进 warningTypeIdList
     */
    @Test
    public void mapperShouldDefineSplitWarningTypeAndSubScenesFilters() throws Exception {
        String content = readUtf8(resolveEventWarningOrderMapperXml());

        assertTrue("应定义 sql id=warningTypeIdFilter",
                content.contains("id=\"warningTypeIdFilter\""));
        assertTrue("应定义 sql id=subScenesOnlyFilter",
                content.contains("id=\"subScenesOnlyFilter\""));
        assertTrue("下游任务过滤应使用 JSON_CONTAINS(sub_scenes)",
                content.contains("JSON_CONTAINS") && content.contains("sub_scenes"));
        assertTrue("下游任务片段应绑定 subSceneIdList",
                content.contains("param.subSceneIdList"));

        // V2.0：禁止 warningTypeIdList 同时 OR JSON_CONTAINS(sub_scenes)
        assertFalse("warningTypeIdList 不得再 OR JSON_CONTAINS(sub_scenes)",
                hasWarningTypeListOrSubScenes(content));
    }

    /**
     * WHEN 列表过滤点 include
     * THEN 应同时 include 类型过滤与下游任务过滤（AND）
     */
    @Test
    public void warningOrderMapperSitesShouldIncludeBothFilters() throws Exception {
        String content = readUtf8(resolveEventWarningOrderMapperXml());

        int typeIncludes = countOccurrences(content, "refid=\"warningTypeIdFilter\"");
        int subIncludes = countOccurrences(content, "refid=\"subScenesOnlyFilter\"");
        int joinIncludes = countOccurrences(content, "refid=\"subScenesOnlyFilterByOrderTables\"");
        assertTrue("warningTypeIdFilter include 期望至少 7 处，实际=" + typeIncludes, typeIncludes >= 7);
        // 月表直筛 6 处 + 领导批示 EXISTS 月表 1 处
        assertTrue("subScenesOnlyFilter include 期望至少 6 处，实际=" + subIncludes, subIncludes >= 6);
        assertTrue("领导批示应 include subScenesOnlyFilterByOrderTables", joinIncludes >= 1);
        assertFalse("不应再 include 旧 subScenesOrWarningTypeFilter",
                content.contains("refid=\"subScenesOrWarningTypeFilter\""));
    }

    /**
     * Spec：告警类型与下游任务 AND — Event 复用 Mapper
     */
    @Test
    public void eventReuseMappersShouldIncludeSplitFilters() throws Exception {
        for (String relative : EVENT_REUSE_MAPPERS) {
            Path xml = resolveUnder("video-event", "src/main/resources/" + relative);
            String content = readUtf8(xml);
            String label = relative;

            assertTrue(label + " 应 include warningTypeIdFilter",
                    content.contains("refid=\"" + EVENT_WARNING_TYPE_REF + "\"")
                            || content.contains("refid=\"warningTypeIdFilter\""));
            assertTrue(label + " 应 include subScenesOnlyFilter",
                    content.contains("refid=\"" + EVENT_SUB_SCENES_REF + "\"")
                            || content.contains("refid=\"subScenesOnlyFilter\""));
            assertFalse(label + " 不得再 OR 混用 warningTypeIdList+sub_scenes",
                    hasWarningTypeListOrSubScenes(content));
        }
    }

    /**
     * Spec：导出模块本地双片段
     */
    @Test
    public void exportReuseMappersShouldDefineLocalSplitFilters() throws Exception {
        for (String relative : EXPORT_REUSE_MAPPERS) {
            Path xml = resolveUnder("video-export", "src/main/resources/" + relative);
            String content = readUtf8(xml);
            String label = relative;

            assertTrue(label + " 应本地定义 warningTypeIdFilter",
                    content.contains("id=\"warningTypeIdFilter\""));
            assertTrue(label + " 应本地定义 subScenesOnlyFilter",
                    content.contains("id=\"subScenesOnlyFilter\""));
            assertTrue(label + " 应绑定 subSceneIdList",
                    content.contains("param.subSceneIdList"));
            assertFalse(label + " 不得再 OR 混用",
                    hasWarningTypeListOrSubScenes(content));
        }
    }

    /**
     * BaseAlarmOrderDTO 应承载下游任务入参
     */
    @Test
    public void baseAlarmOrderDtoShouldDeclareSubSceneIdList() throws Exception {
        Path javaFile = resolveUnder("video-api-event",
                "src/main/java/com/chinatower/event/entity/dto/BaseAlarmOrderDTO.java");
        // video-api 不在 video-services 下
        if (!Files.exists(javaFile)) {
            javaFile = Paths.get("../../video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/BaseAlarmOrderDTO.java");
            if (!Files.exists(javaFile)) {
                javaFile = Paths.get("video-api/video-api-event/src/main/java/com/chinatower/event/entity/dto/BaseAlarmOrderDTO.java");
            }
        }
        assertTrue("找不到 BaseAlarmOrderDTO: " + javaFile.toAbsolutePath(), Files.exists(javaFile));
        String content = readUtf8(javaFile);
        assertTrue("BaseAlarmOrderDTO 应声明 subSceneIdList",
                content.contains("subSceneIdList"));
    }

    /** 旧 OR 语义特征：同一 if(warningTypeIdList) 块内同时出现 warning_type_id IN 与 JSON_CONTAINS(sub_scenes) */
    private static boolean hasWarningTypeListOrSubScenes(String content) {
        int idx = 0;
        while ((idx = content.indexOf("param.warningTypeIdList", idx)) >= 0) {
            int end = content.indexOf("</if>", idx);
            if (end < 0) {
                break;
            }
            String block = content.substring(idx, end);
            if (block.contains("warning_type_id") && block.contains("JSON_CONTAINS")
                    && block.contains("sub_scenes")) {
                return true;
            }
            idx = end + 5;
        }
        return false;
    }

    private static String readUtf8(Path xml) throws Exception {
        return new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);
    }

    private static Path resolveEventWarningOrderMapperXml() {
        return resolveUnder("video-event",
                "src/main/resources/com/chinatower/event/mapper/WarningOrderMapper.xml");
    }

    private static Path resolveUnder(String module, String resourceRelative) {
        Path fromSibling = Paths.get("../" + module + "/" + resourceRelative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        // video-api 模块
        Path fromApiSibling = Paths.get("../../video-api/" + module + "/" + resourceRelative);
        if (Files.exists(fromApiSibling)) {
            return fromApiSibling;
        }
        Path fromRepo = Paths.get("video-services/" + module + "/" + resourceRelative);
        if (Files.exists(fromRepo)) {
            return fromRepo;
        }
        Path fromRepoApi = Paths.get("video-api/" + module + "/" + resourceRelative);
        assertTrue("找不到资源: " + module + "/" + resourceRelative,
                Files.exists(fromRepoApi));
        return fromRepoApi;
    }

    private static int countOccurrences(String content, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = content.indexOf(token, idx)) >= 0) {
            count++;
            idx += token.length();
        }
        return count;
    }
}
