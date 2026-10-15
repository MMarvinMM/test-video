package com.chinatower.event.util.subscenes;

import com.chinatower.event.entity.dto.OperateMongo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Spec：Operate Mongo sub_scenes 与月表对齐；刷数同步不得用场景主类型覆盖下游编码。
 */
public class SubScenesMongoHelperTest {

    private static final String WARNING_TYPE = "10313124";
    private static final String DOWNSTREAM = "9212102";

    @Test
    public void parseSubScenesListShouldReturnJsonArray() {
        List<String> codes = SubScenesMongoHelper.parseSubScenesList(
                "[\"10313124\",\"10212138\"]", WARNING_TYPE);
        Assert.assertEquals(Arrays.asList("10313124", "10212138"), codes);
    }

    @Test
    public void parseSubScenesListShouldFallbackToWarningType() {
        List<String> codes = SubScenesMongoHelper.parseSubScenesList(null, WARNING_TYPE);
        Assert.assertEquals(Collections.singletonList(WARNING_TYPE), codes);
    }

    /**
     * 场景：回填同步 Operate Mongo
     * WHEN 月表 sub_scenes 为空
     * THEN 精确解析为 null，不得回退场景主类型
     */
    @Test
    public void parseSubScenesExactShouldNotFallbackToWarningType() {
        Assert.assertNull(SubScenesMongoHelper.parseSubScenesExact(null));
        Assert.assertNull(SubScenesMongoHelper.parseSubScenesExact("   "));
        Assert.assertNull(SubScenesMongoHelper.parseSubScenesExact("[]"));
    }

    /**
     * WHEN 月表 sub_scenes=["下游A"]
     * THEN 精确解析仅含下游 A
     */
    @Test
    public void parseSubScenesExactShouldKeepDownstreamCodes() {
        List<String> codes = SubScenesMongoHelper.parseSubScenesExact("[\"" + DOWNSTREAM + "\"]");
        Assert.assertEquals(Collections.singletonList(DOWNSTREAM), codes);
    }

    @Test
    public void applyToOperateMongoShouldSetSubScenesField() {
        OperateMongo mongo = new OperateMongo();
        SubScenesMongoHelper.applyToOperateMongo(mongo, "[\"A\",\"B\"]", WARNING_TYPE);
        Assert.assertEquals(Arrays.asList("A", "B"), mongo.getSubScenes());
    }

    /**
     * 写路径与月表一致：VO.subScenes 为空时不按 alarmType 兜底写入 Mongo
     */
    @Test
    public void applyToOperateMongoShouldNotFallbackWhenSubScenesBlank() {
        OperateMongo mongo = new OperateMongo();
        SubScenesMongoHelper.applyToOperateMongo(mongo, null, WARNING_TYPE);
        Assert.assertNull(mongo.getSubScenes());
    }

    /**
     * Spec V2.0：Mongo 下游任务过滤仅打 sub_scenes
     */
    @Test
    public void subScenesOnlyCriteriaShouldFilterSubScenesField() {
        Assert.assertNull(SubScenesMongoHelper.subScenesOnlyCriteria(null));
        Assert.assertNull(SubScenesMongoHelper.subScenesOnlyCriteria(Collections.emptyList()));
        Assert.assertEquals("sub_scenes",
                SubScenesMongoHelper.subScenesOnlyCriteria(Collections.singletonList("A"))
                        .getKey());
    }
}
