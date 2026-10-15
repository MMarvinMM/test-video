package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec V2.0：客户导出新增下游任务 sheet/章节九；原告警类型产物利旧。
 */
public class CustomerExportSubSceneContractTest {

    @Test
    public void exportParamShouldCarrySubSceneSheets() throws Exception {
        Path param = resolveApi(
                "src/main/java/com/chinatower/analysis/api/entity/StatisticsExportParam.java");
        String content = readUtf8(param);
        assertTrue(content.contains("eventSheetSubScene"));
        assertTrue(content.contains("eventSheetSubSceneAccuracy"));
    }

    @Test
    public void excelExportShouldAddSubSceneSheetsAfterTypeSheets() throws Exception {
        Path javaFile = resolveExport(
                "src/main/java/com/chinatower/platform/service/impl/ExportStatisticsServiceImpl.java");
        String content = readUtf8(javaFile);
        assertTrue("应创建下游任务排行 sheet",
                content.contains("下游任务排行"));
        assertTrue("应创建下游任务准确率排行 sheet",
                content.contains("下游任务准确率排行"));
        assertTrue("应保留告警类型排行",
                content.contains("告警类型排行"));
        assertTrue("应保留告警类型准确率排行",
                content.contains("告警类型准确率排行"));
        assertTrue("下游任务数据应带 SUB_TASK 维度",
                content.contains("SUB_TASK"));
    }

    @Test
    public void wordExportShouldAppendSubSceneChapterNine() throws Exception {
        Path javaFile = resolveExport(
                "src/main/java/com/chinatower/platform/service/impl/StatisticWordServiceImpl.java");
        String content = readUtf8(javaFile);
        assertTrue("报告应新增章节九下游任务统计",
                content.contains("九、下游任务统计")
                        || content.contains("九、下游任务"));
        assertTrue("应拉取 SUB_TASK 维度数据",
                content.contains("SUB_TASK"));
        assertTrue("应保留告警类型统计章节语义",
                content.contains("告警类型统计") || content.contains("表6 告警类型统计"));
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveExport(String relative) {
        Path fromSibling = Paths.get("../video-export/" + relative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-services/video-export/" + relative);
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
