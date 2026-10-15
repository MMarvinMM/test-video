package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec：工单持久化实际触发子任务编码（Task 3 — Mapper INSERT 列）。
 * 轻量断言 insertWarningOrders SQL 含 sub_scenes / #{item.subScenes}。
 */
public class InsertWarningOrdersSubScenesXmlTest {

    /**
     * WHEN 接警月表 INSERT
     * THEN video-receive WarningOrderMapper.insertWarningOrders 含 sub_scenes 列绑定
     */
    @Test
    public void receiveMapperInsertShouldContainSubScenesColumn() throws Exception {
        Path xml = resolveReceiveMapperXml();
        String content = new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);

        assertTrue("insertWarningOrders 列清单应含 sub_scenes",
                content.contains("sub_scenes"));
        assertTrue("insertWarningOrders VALUES 应绑定 #{item.subScenes}",
                content.contains("#{item.subScenes}"));
    }

    /**
     * WHEN video-event 镜像 INSERT
     * THEN 同样包含 sub_scenes 列绑定（与 receive 一致）
     */
    @Test
    public void eventMapperInsertShouldContainSubScenesColumn() throws Exception {
        Path xml = resolveEventMapperXml();
        String content = new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);

        // 仅检查 insertWarningOrders 片段附近：用第二次出现前的 insert 块粗略校验
        int insertIdx = content.indexOf("insert id=\"insertWarningOrders\"");
        assertTrue("应存在 insertWarningOrders", insertIdx >= 0);
        String insertBlock = content.substring(insertIdx, Math.min(content.length(), insertIdx + 3500));
        assertTrue("event insertWarningOrders 列清单应含 sub_scenes",
                insertBlock.contains("sub_scenes"));
        assertTrue("event insertWarningOrders VALUES 应绑定 #{item.subScenes}",
                insertBlock.contains("#{item.subScenes}"));
    }

    private static Path resolveReceiveMapperXml() {
        // 测试运行 cwd 一般为 video-receive 模块根
        Path fromModule = Paths.get("src/main/resources/com.chinatower.receive.mapper/WarningOrderMapper.xml");
        if (Files.exists(fromModule)) {
            return fromModule;
        }
        Path fromRepo = Paths.get("video-services/video-receive/src/main/resources/"
                + "com.chinatower.receive.mapper/WarningOrderMapper.xml");
        assertTrue("找不到 video-receive WarningOrderMapper.xml: " + fromRepo.toAbsolutePath(),
                Files.exists(fromRepo));
        return fromRepo;
    }

    private static Path resolveEventMapperXml() {
        Path fromSibling = Paths.get("../video-event/src/main/resources/"
                + "com/chinatower/event/mapper/WarningOrderMapper.xml");
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-services/video-event/src/main/resources/"
                + "com/chinatower/event/mapper/WarningOrderMapper.xml");
        assertTrue("找不到 video-event WarningOrderMapper.xml: " + fromRepo.toAbsolutePath(),
                Files.exists(fromRepo));
        return fromRepo;
    }
}
