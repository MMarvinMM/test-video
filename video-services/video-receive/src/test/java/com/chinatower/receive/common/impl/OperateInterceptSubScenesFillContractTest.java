package com.chinatower.receive.common.impl;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spec：运营研判 / 拦截写 Mongo 前须 fillSubScenes，与月表 saveOrder 一致。
 * <p>否则 Kafka 链路丢失 subScenes 时，旧 Helper 会用场景 alarmType 兜底写入 Mongo，
 * 导致按下游编码筛研判列表漏掉场景工单。</p>
 */
public class OperateInterceptSubScenesFillContractTest {

    @Test
    public void saveOperateShouldFillSubScenesBeforeMongoInsert() throws Exception {
        String content = readCommonService();
        int saveOperate = content.indexOf("public void saveOperate");
        Assert.assertTrue(saveOperate > 0);
        int nextMethod = content.indexOf("public void ", saveOperate + 10);
        String block = content.substring(saveOperate, nextMethod > 0 ? nextMethod : content.length());
        Assert.assertTrue("saveOperate 入库/写 Mongo 前应 fillSubScenes",
                block.contains("SubScenesWriteHelper.fillSubScenes")
                        || block.contains("fillSubScenes"));
        Assert.assertTrue("fill 须在 insertOperate 之前",
                block.indexOf("fillSubScenes") < block.indexOf("insertOperate")
                        || block.indexOf("SubScenesWriteHelper") < block.indexOf("insertOperate"));
        Assert.assertTrue("fill 须在 insertAlarmOperates 之前",
                block.indexOf("fillSubScenes") < block.indexOf("insertAlarmOperates")
                        || block.indexOf("SubScenesWriteHelper") < block.indexOf("insertAlarmOperates"));
    }

    @Test
    public void insertAlarmOperatesShouldPersistSubScenesColumn() throws Exception {
        Path file = Paths.get(
                "src/main/resources/com.chinatower.receive.mapper/AlarmOperateMapper.xml");
        if (!Files.exists(file)) {
            file = Paths.get(
                    "video-services/video-receive/src/main/resources/com.chinatower.receive.mapper/AlarmOperateMapper.xml");
        }
        Assert.assertTrue(Files.exists(file));
        String xml = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        int start = xml.indexOf("id=\"insertAlarmOperates\"");
        Assert.assertTrue(start > 0);
        String block = xml.substring(start, xml.indexOf("</insert>", start));
        Assert.assertTrue("insertAlarmOperates 列清单须含 sub_scenes",
                block.contains("sub_scenes"));
        Assert.assertTrue("insertAlarmOperates 须写入 item.subScenes",
                block.contains("#{item.subScenes}"));
    }

    @Test
    public void saveInterceptShouldFillSubScenesBeforeMongoInsert() throws Exception {
        String content = readCommonService();
        int saveIntercept = content.indexOf("public void saveIntercept");
        Assert.assertTrue(saveIntercept > 0);
        int nextMethod = content.indexOf("public void ", saveIntercept + 10);
        String block = content.substring(saveIntercept, nextMethod > 0 ? nextMethod : content.length());
        Assert.assertTrue("saveIntercept 写 Mongo 前应 fillSubScenes",
                block.contains("SubScenesWriteHelper.fillSubScenes")
                        || block.contains("fillSubScenes"));
        Assert.assertTrue("fill 须在 insertIntercept 之前",
                block.indexOf("fillSubScenes") < block.indexOf("insertIntercept")
                        || block.indexOf("SubScenesWriteHelper") < block.indexOf("insertIntercept"));
    }

    private static String readCommonService() throws Exception {
        Path file = Paths.get(
                "src/main/java/com/chinatower/receive/common/impl/CommonServiceImpl.java");
        if (!Files.exists(file)) {
            file = Paths.get(
                    "video-services/video-receive/src/main/java/com/chinatower/receive/common/impl/CommonServiceImpl.java");
        }
        Assert.assertTrue(Files.exists(file));
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }
}
