package com.chinatower.receive.core.alarm.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Spec V2.0：客户下游任务下拉 — 历史订购过大模型即展示下游任务（含退订/到期），支持名称模糊。
 */
public class FindOrderSubSceneAlarmContractTest {

    /**
     * WHEN 定义下游任务下拉查询
     * THEN Mapper SQL：当前订购 ∪ 历史订购 + 平台 model_config 下游目录；
     *      支持 typeName 模糊与大模型算法编码过滤；不依赖场景表；不限制 ord_status
     */
    @Test
    public void alarmTypeMapperShouldQueryOrderedLlmSubScenes() throws Exception {
        Path xml = resolveUnder("video-event",
                "src/main/resources/com/chinatower/event/mapper/AlarmTypeMapper.xml");
        String content = readUtf8(xml);
        String block = extractSelectBlock(content, "findOrderSubSceneAlarm");

        assertTrue("应定义 findOrderSubSceneAlarm",
                content.contains("id=\"findOrderSubSceneAlarm\""));
        assertTrue("应包含当前订购表",
                block.contains("vm_order_algorithms"));
        assertTrue("应包含历史订购表（退订后仍可追溯）",
                block.contains("vm_order_algorithms_his"));
        assertTrue("下游目录应来自平台 model_config",
                block.contains("model_config") && block.contains("JSON_TABLE"));
        assertTrue("不应再依赖租户场景告警关系表",
                !block.contains("sys_scene_alarm_rel") && !block.contains("sys_scene_config"));
        assertTrue("应绑定大模型算法编码列表",
                block.contains("body.algorithmCodes") || block.contains("body.modelAlgorithmCodeList"));
        assertTrue("应支持名称模糊",
                block.contains("typeName") && block.contains("LIKE"));
        // 历史订购口径：不得仅限有效订购状态 0/1/2
        assertTrue("下游下拉不应限制 ord_status IN (0,1,2)",
                !block.matches("(?s).*ord_status\\s+IN\\s*\\(\\s*0\\s*,\\s*1\\s*,\\s*2\\s*\\).*")
                        && !block.contains("ord_status IN (0, 1, 2)"));
    }

    /**
     * WHEN Repository 组装下游下拉
     * THEN 注释/逻辑说明历史订购（含退订到期）可见
     */
    @Test
    public void repositoryShouldDocumentHistoryOrderedVisibility() throws Exception {
        Path repo = resolveUnder("video-event",
                "src/main/java/com/chinatower/event/repository/impl/AlarmTypeRepositoryImpl.java");
        String method = extractMethod(readUtf8(repo), "findOrderSubSceneAlarm");
        assertTrue("应说明历史订购/退订到期仍展示，或不再写「仅已订购」",
                method.contains("历史") || method.contains("退订") || method.contains("到期")
                        || !method.contains("仅已订购"));
    }

    /** 截取指定 select 代码块 */
    private static String extractSelectBlock(String xml, String selectId) {
        String marker = "id=\"" + selectId + "\"";
        int start = xml.indexOf(marker);
        assertTrue("找不到 select: " + selectId, start >= 0);
        int end = xml.indexOf("</select>", start);
        assertTrue("找不到 </select>: " + selectId, end > start);
        return xml.substring(start, end);
    }

    private static String extractMethod(String java, String methodName) {
        int start = java.indexOf(methodName + "(");
        assertTrue("找不到方法: " + methodName, start >= 0);
        // 向上找注释/注解起点
        int comment = java.lastIndexOf("/**", start);
        if (comment < 0 || start - comment > 400) {
            comment = start;
        }
        int brace = java.indexOf('{', start);
        int depth = 0;
        int end = brace;
        for (int i = brace; i < java.length(); i++) {
            char c = java.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    end = i;
                    break;
                }
            }
        }
        return java.substring(comment, end + 1);
    }

    /**
     * WHEN 暴露下游任务下拉接口
     * THEN Controller 提供 /findOrderSubSceneAlarm；Repository 提供同名方法
     */
    @Test
    public void controllerAndRepositoryShouldExposeFindOrderSubSceneAlarm() throws Exception {
        Path controller = resolveUnder("video-event",
                "src/main/java/com/chinatower/event/controller/AlarmTypeController.java");
        Path repository = resolveUnder("video-event",
                "src/main/java/com/chinatower/event/repository/IAlarmTypeRepository.java");
        Path mapper = resolveUnder("video-event",
                "src/main/java/com/chinatower/event/mapper/AlarmTypeMapper.java");

        assertTrue(readUtf8(controller).contains("findOrderSubSceneAlarm"));
        assertTrue(readUtf8(repository).contains("findOrderSubSceneAlarm"));
        assertTrue(readUtf8(mapper).contains("findOrderSubSceneAlarm"));
    }

    private static String readUtf8(Path path) throws Exception {
        assertTrue("找不到文件: " + path.toAbsolutePath(), Files.exists(path));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Path resolveUnder(String module, String resourceRelative) {
        Path fromSibling = Paths.get("../" + module + "/" + resourceRelative);
        if (Files.exists(fromSibling)) {
            return fromSibling;
        }
        Path fromRepo = Paths.get("video-services/" + module + "/" + resourceRelative);
        assertTrue("找不到资源: " + module + "/" + resourceRelative, Files.exists(fromRepo));
        return fromRepo;
    }
}
