package com.chinatower.event.util.subscenes;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 领导批示下游筛选：按告警时间范围展开工单月表名。
 */
public class SubScenesOrderTableHelperTest {

    @Test
    public void shouldListTablesAcrossMonths() {
        List<String> tables = SubScenesOrderTableHelper.listTables(
                "2026-08-01 00:00:00", "2026-09-15 23:59:59");
        assertEquals(2, tables.size());
        assertEquals("vm_warning_order_t_202608", tables.get(0));
        assertEquals("vm_warning_order_t_202609", tables.get(1));
    }

    @Test
    public void shouldAcceptSingleDigitMonthDay() {
        List<String> tables = SubScenesOrderTableHelper.listTables(
                "2026-9-9 00:00:00", "2026-9-15 23:59:59");
        assertEquals(1, tables.size());
        assertEquals("vm_warning_order_t_202609", tables.get(0));
    }

    @Test
    public void shouldReturnEmptyWhenTimeBlank() {
        assertTrue(SubScenesOrderTableHelper.listTables(null, "2026-09-15 23:59:59").isEmpty());
        assertTrue(SubScenesOrderTableHelper.listTables("2026-09-01 00:00:00", "").isEmpty());
    }
}
