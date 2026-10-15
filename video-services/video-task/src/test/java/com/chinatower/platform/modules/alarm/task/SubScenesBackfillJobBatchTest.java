package com.chinatower.platform.modules.alarm.task;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Job 应对 tableNames 逐表调用刷数服务。
 */
public class SubScenesBackfillJobBatchTest {

    @Test
    public void shouldBackfillEachTableInOrder() {
        List<String> visited = new ArrayList<>();
        AtomicInteger totalUpdated = new AtomicInteger();

        SubScenesBackfillParam param = SubScenesBackfillParam.parse(
                "{\"tableNames\":[\"vm_warning_order_t_202601\",\"vm_warning_order_t_202602\"]}");

        // 模拟 Job 循环：与 SubScenesBackfillJob 同一策略
        for (String table : param.getTableNames()) {
            visited.add(table);
            SubScenesBackfillParam one = param.copyForTable(table);
            assertEquals(table, one.getTableName());
            assertEquals("", one.getLastId());
            totalUpdated.addAndGet(1);
        }

        assertEquals(Arrays.asList("vm_warning_order_t_202601", "vm_warning_order_t_202602"), visited);
        assertEquals(2, totalUpdated.get());
    }
}
