package com.chinatower.event.core.alarm.util.subscenes;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spec：运营研判入库须落库 sub_scenes，且 insert 前 fillSubScenes。
 */
public class OperateSubScenesPersistContractTest {

    @Test
    public void insertAlarmOperatesShouldPersistSubScenesColumn() throws Exception {
        String xml = read("src/main/resources/com/chinatower/event/mapper/AlarmOperateMapper.xml",
                "video-services/video-event/src/main/resources/com/chinatower/event/mapper/AlarmOperateMapper.xml");
        int start = xml.indexOf("id=\"insertAlarmOperates\"");
        Assert.assertTrue(start > 0);
        String block = xml.substring(start, xml.indexOf("</insert>", start));
        Assert.assertTrue(block.contains("sub_scenes"));
        Assert.assertTrue(block.contains("#{item.subScenes}"));
    }

    @Test
    public void batchSaveHandlersShouldFillSubScenesBeforeInsert() throws Exception {
        assertFillBeforeInsert(
                "src/main/java/com/chinatower/event/core/alarm/handler/BatchSaveHandler.java",
                "video-services/video-event/src/main/java/com/chinatower/event/core/alarm/handler/BatchSaveHandler.java");
        assertFillBeforeInsert(
                "src/main/java/com/chinatower/event/core/alarm/handler/IotBatchSaveHandler.java",
                "video-services/video-event/src/main/java/com/chinatower/event/core/alarm/handler/IotBatchSaveHandler.java");
        assertFillBeforeInsert(
                "src/main/java/com/chinatower/event/repository/impl/ReceiveRepositoryImpl.java",
                "video-services/video-event/src/main/java/com/chinatower/event/repository/impl/ReceiveRepositoryImpl.java");
    }

    private static void assertFillBeforeInsert(String relative, String fallback) throws Exception {
        String java = read(relative, fallback);
        Assert.assertTrue(relative + " 应 fillSubScenes",
                java.contains("SubScenesWriteHelper.fillSubScenes")
                        || java.contains("fillSubScenes"));
        int fill = Math.max(java.indexOf("fillSubScenes"), java.indexOf("SubScenesWriteHelper"));
        int insert = java.indexOf("insertAlarmOperates");
        Assert.assertTrue(relative + " fill 须在 insertAlarmOperates 之前",
                fill >= 0 && insert > fill);
    }

    private static String read(String relative, String fallback) throws Exception {
        Path file = Paths.get(relative);
        if (!Files.exists(file)) {
            file = Paths.get(fallback);
        }
        Assert.assertTrue("找不到: " + file, Files.exists(file));
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }
}
