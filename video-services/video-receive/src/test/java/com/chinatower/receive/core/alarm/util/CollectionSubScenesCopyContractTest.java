package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec：收藏写入应从月表带上实际触发下游任务编码 sub_scenes。
 */
public class CollectionSubScenesCopyContractTest {

    /**
     * WHEN 收藏从月表拷贝工单
     * THEN warningOrderFields 含 t.sub_scenes，实体含 subScenes
     */
    @Test
    public void collectionCopyFromMonthTableShouldIncludeSubScenes() throws Exception {
        String xml = readUtf8(resolveEventCollectionMapperXml());
        String fields = extractSqlBlock(xml, "warningOrderFields");
        assertTrue("月表拷贝字段应含 t.sub_scenes", fields.contains("t.sub_scenes"));

        String operate = extractSqlBlock(xml, "alarmOperateOrderFields");
        assertTrue("研判表无 sub_scenes 列，不得直接选 t.sub_scenes",
                !operate.contains("t.sub_scenes"));
        String intercept = extractSqlBlock(xml, "alarmInterceptOrderFields");
        assertTrue("拦截表无 sub_scenes 列，不得直接选 t.sub_scenes",
                !intercept.contains("t.sub_scenes"));

        String java = readUtf8(resolveCollectionDo());
        assertTrue("WarningOrderCollectionDO 应声明 subScenes",
                java.contains("subScenes"));
    }

    private static String extractSqlBlock(String xml, String sqlId) {
        String marker = "id=\"" + sqlId + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 sql: " + sqlId, start >= 0);
        int end = xml.indexOf("</sql>", start);
        assertTrue("找不到 </sql>: " + sqlId, end > start);
        return xml.substring(start, end);
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到文件: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveEventCollectionMapperXml() {
        Path fromReceive = Paths.get("../video-event/src/main/resources/"
                + "com/chinatower/event/mapper/WarningOrderCollectionMapper.xml");
        if (Files.exists(fromReceive)) {
            return fromReceive;
        }
        Path fromRepo = Paths.get("video-services/video-event/src/main/resources/"
                + "com/chinatower/event/mapper/WarningOrderCollectionMapper.xml");
        assertTrue("找不到 WarningOrderCollectionMapper.xml", Files.exists(fromRepo));
        return fromRepo;
    }

    private static Path resolveCollectionDo() {
        Path fromReceive = Paths.get("../../../video-api/video-api-event/src/main/java/"
                + "com/chinatower/event/dataobject/WarningOrderCollectionDO.java");
        if (Files.exists(fromReceive)) {
            return fromReceive;
        }
        Path fromRepo = Paths.get("video-api/video-api-event/src/main/java/"
                + "com/chinatower/event/dataobject/WarningOrderCollectionDO.java");
        assertTrue("找不到 WarningOrderCollectionDO.java", Files.exists(fromRepo));
        return fromRepo;
    }
}
