package com.chinatower.platform.modules.alarm.service;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spec：回填同步 Operate Mongo — 月表刷数后须同步运营/拦截 Mongo sub_scenes。
 */
public class SubScenesBackfillMongoSyncContractTest {

    @Test
    public void backfillServiceShouldSyncOperateAndInterceptMongo() throws Exception {
        Path file = Paths.get(
                "src/main/java/com/chinatower/platform/modules/alarm/service/SubScenesBackfillService.java");
        if (!Files.exists(file)) {
            file = Paths.get(
                    "video-services/video-task/src/main/java/com/chinatower/platform/modules/alarm/service/SubScenesBackfillService.java");
        }
        Assert.assertTrue(Files.exists(file));
        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        Assert.assertTrue("应注入/调用 OperateMongoMapper 同步",
                content.contains("OperateMongoMapper") && content.contains("syncSubScenes"));
        Assert.assertTrue("应同步 Intercept Mongo",
                content.contains("InterceptMongoMapper") && content.contains("syncSubScenes"));
        Assert.assertTrue("月表已正确时仍应同步 Mongo（不得仅 MySQL 相等就整行跳过）",
                content.contains("syncMongoSubScenes") || content.contains("syncOperate"));
    }

    @Test
    public void operateMongoMapperShouldExposeSyncSubScenes() throws Exception {
        Path file = Paths.get(
                "src/main/java/com/chinatower/platform/mongo/OperateMongoMapper.java");
        if (!Files.exists(file)) {
            file = Paths.get(
                    "video-services/video-task/src/main/java/com/chinatower/platform/mongo/OperateMongoMapper.java");
        }
        Assert.assertTrue(Files.exists(file));
        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        Assert.assertTrue(content.contains("syncSubScenes"));
        Assert.assertTrue("同步须用精确解析，不得 warningTypeId 兜底",
                content.contains("parseSubScenesExact")
                        || content.contains("FIELD_SUB_SCENES"));
    }
}
