package com.chinatower.platform.modules.alarm.task;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Spec：历史 sub_scenes 可回填 — XXL Job 参数解析。
 */
public class SubScenesBackfillParamTest {

    @Test
    public void shouldParseFullJsonParam() {
        SubScenesBackfillParam param = SubScenesBackfillParam.parse(
                "{\"tableName\":\"vm_warning_order_t_202608\",\"batchSize\":2000,\"lastId\":\"abc\"}");

        assertEquals("vm_warning_order_t_202608", param.getTableName());
        assertEquals(Arrays.asList("vm_warning_order_t_202608"), param.getTableNames());
        assertEquals(2000, param.getBatchSize());
        assertEquals("abc", param.getLastId());
    }

    @Test
    public void shouldUseDefaultsWhenOptionalBlank() {
        SubScenesBackfillParam param = SubScenesBackfillParam.parse(
                "{\"tableName\":\"vm_warning_order_t_202608\"}");

        assertEquals("vm_warning_order_t_202608", param.getTableName());
        assertEquals(SubScenesBackfillParam.DEFAULT_BATCH_SIZE, param.getBatchSize());
        assertEquals("", param.getLastId());
    }

    /**
     * WHEN 传 tableNames 数组
     * THEN 按序解析多张合法月表
     */
    @Test
    public void shouldParseTableNamesArray() {
        SubScenesBackfillParam param = SubScenesBackfillParam.parse(
                "{\"tableNames\":[\"vm_warning_order_t_202601\",\"vm_warning_order_t_202609\"],\"batchSize\":1000}");

        List<String> names = param.getTableNames();
        assertEquals(2, names.size());
        assertEquals("vm_warning_order_t_202601", names.get(0));
        assertEquals("vm_warning_order_t_202609", names.get(1));
        assertEquals("vm_warning_order_t_202601", param.getTableName());
        assertEquals(1000, param.getBatchSize());
    }

    /**
     * WHEN 同时传 tableName 与 tableNames
     * THEN 合并去重（tableName 在前）
     */
    @Test
    public void shouldMergeTableNameAndTableNames() {
        SubScenesBackfillParam param = SubScenesBackfillParam.parse(
                "{\"tableName\":\"vm_warning_order_t_202608\","
                        + "\"tableNames\":[\"vm_warning_order_t_202608\",\"vm_warning_order_t_202609\"]}");

        assertEquals(Arrays.asList("vm_warning_order_t_202608", "vm_warning_order_t_202609"),
                param.getTableNames());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectBlankTableName() {
        SubScenesBackfillParam.parse("{}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnsafeTableName() {
        SubScenesBackfillParam.parse("{\"tableName\":\"vm_warning_order_t_202608;drop\"}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnsafeNameInTableNames() {
        SubScenesBackfillParam.parse(
                "{\"tableNames\":[\"vm_warning_order_t_202608\",\"bad_table\"]}");
    }

    @Test
    public void shouldAcceptEmptyParamAsInvalidWithoutThrowingParseOfNullShape() {
        try {
            SubScenesBackfillParam.parse(null);
            assertTrue("应拒绝空参数", false);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("tableName"));
        }
    }

    /**
     * WHEN 多表刷数
     * THEN lastId 仅单表时生效；多表时各表从空游标开始
     */
    @Test
    public void lastIdShouldApplyOnlyForSingleTable() {
        SubScenesBackfillParam multi = SubScenesBackfillParam.parse(
                "{\"tableNames\":[\"vm_warning_order_t_202601\",\"vm_warning_order_t_202602\"],\"lastId\":\"x\"}");
        assertEquals("", multi.resolveLastIdForTable("vm_warning_order_t_202601"));
        assertEquals("", multi.resolveLastIdForTable("vm_warning_order_t_202602"));

        SubScenesBackfillParam single = SubScenesBackfillParam.parse(
                "{\"tableName\":\"vm_warning_order_t_202601\",\"lastId\":\"x\"}");
        assertEquals("x", single.resolveLastIdForTable("vm_warning_order_t_202601"));
    }
}
