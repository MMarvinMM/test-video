package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec：getOrderList 回填告警类型名 — 大模型工单 warning_type_id 为场景 UUID(source=3)，
 * findOrderAlarmAll 须能查出该 UUID，否则列表 warningTypeName 为空。
 */
public class FindOrderAlarmAllSceneNameContractTest {

    @Test
    public void findOrderAlarmAllShouldIncludeTenantSceneTypes() throws Exception {
        Path eventXml = resolve("video-services/video-event/src/main/resources/com/chinatower/event/mapper/AlarmTypeMapper.xml");
        Path exportXml = resolve("video-services/video-export/src/main/resources/com/chinatower/platform/mapper/IAlarmTypeMapper.xml");
        assertContainsSceneUnion(extractSelectBlock(readUtf8(eventXml), "findOrderAlarmAll"));
        assertContainsSceneUnion(extractSelectBlock(readUtf8(exportXml), "findOrderAlarmAll"));
    }

    private static void assertContainsSceneUnion(String block) {
        assertTrue("应补齐租户大模型场景(source=3)供列表回填名称",
                block.contains("source") && block.contains("= 3")
                        && block.contains("body.tenantId")
                        && block.contains("body.industryCode"));
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

    private static Path resolve(String relative) {
        Path fromRepo = Paths.get(relative);
        if (Files.exists(fromRepo)) {
            return fromRepo;
        }
        Path fromReceive = Paths.get("../" + relative.replace("video-services/", ""));
        if (Files.exists(fromReceive)) {
            return fromReceive;
        }
        Path nested = Paths.get("../../" + relative);
        assertTrue("找不到: " + relative, Files.exists(nested));
        return nested;
    }
}
