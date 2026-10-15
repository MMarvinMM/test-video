package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec：区域办结(Industry) 支持告警类型 ∧ 下游任务组合过滤（方案 A）。
 * 有 subSceneIdList 时读带 warning_type_id 的下游日汇总；两路条件 AND。
 */
public class EvenAreaCompletionIndustrySubSceneContractTest {

    /**
     * WHEN AnalysisVO 承接工作台筛选
     * THEN 必须声明 subSceneIdList（与列表侧命名对齐）
     */
    @Test
    public void analysisVoShouldDeclareSubSceneIdList() throws Exception {
        Path vo = resolveApi("src/main/java/com/chinatower/analysis/api/entity/AnalysisVO.java");
        assertTrue("AnalysisVO 应声明 subSceneIdList",
                readUtf8(vo).contains("subSceneIdList"));
    }

    /**
     * WHEN 仅传下游 / 同时传场景与下游
     * THEN Industry SQL 切 vm_statistics_event_sub_scene，并 AND 过滤类型与下游
     */
    @Test
    public void industryAreaCompletionShouldAndFilterTypeAndSubScene() throws Exception {
        Path xml = resolveUnder("src/main/resources/mapper/workbench/WorkbenchDAO.xml");
        String block = extractSelectBlock(readUtf8(xml), "getEvenAreaCompletionIndustry");

        assertTrue("有下游时应读下游日汇总表",
                block.contains("subSceneIdList")
                        && block.contains("vm_statistics_event_sub_scene"));
        assertTrue("下游多值应按 sub_scene_code IN 过滤",
                block.contains("sub_scene_code"));
        assertTrue("场景与下游同时传时应 AND warning_type_id",
                block.contains("warning_type_id")
                        && block.contains("warningTypeList"));
        // 无下游时仍走原表，避免破坏现网
        assertTrue("无下游应保留原日汇总表",
                block.contains("vm_statistics_event_num_day"));
    }

    private static String extractSelectBlock(String content, String id) {
        String startToken = "id=\"" + id + "\"";
        int start = content.indexOf(startToken);
        assertTrue("找不到 select: " + id, start >= 0);
        int end = content.indexOf("</select>", start);
        assertTrue("找不到 </select>: " + id, end > start);
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
