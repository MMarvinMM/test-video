package com.chinatower.platform.modules.alarm.task;

import com.chinatower.platform.modules.alarm.service.SubScenesBackfillService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 历史月表 sub_scenes 刷数 XXL Job。
 * <p>
 * 单表：{"tableName":"vm_warning_order_t_202608","batchSize":2000,"lastId":""}<br>
 * 多表：{"tableNames":["vm_warning_order_t_202601","vm_warning_order_t_202609"]}<br>
 * 可重跑：幂等更新；非大模型/解析失败写 null，不按告警类型兜底。多表时 lastId 忽略。
 * </p>
 */
@Slf4j
@Component
public class SubScenesBackfillJob {

    @Autowired
    private SubScenesBackfillService subScenesBackfillService;

    @XxlJob("subScenesBackfillHandler")
    public ReturnT<String> subScenesBackfill(String param) {
        long start = System.currentTimeMillis();
        XxlJobLogger.log("subScenesBackfillHandler开始, param={}", param);
        log.info("subScenesBackfillHandler开始, param={}", param);
        try {
            SubScenesBackfillParam backfillParam = SubScenesBackfillParam.parse(param);
            int updated = 0;
            for (String table : backfillParam.getTableNames()) {
                SubScenesBackfillParam one = backfillParam.copyForTable(table);
                XxlJobLogger.log("subScenesBackfillHandler刷表开始, table={}", table);
                log.info("subScenesBackfillHandler刷表开始, table={}", table);
                int part = subScenesBackfillService.backfill(one);
                updated += part;
                XxlJobLogger.log("subScenesBackfillHandler刷表结束, table={}, updated={}", table, part);
                log.info("subScenesBackfillHandler刷表结束, table={}, updated={}", table, part);
            }
            long cost = System.currentTimeMillis() - start;
            XxlJobLogger.log("subScenesBackfillHandler结束, tables={}, updated={}, 耗时={}ms",
                    backfillParam.getTableNames().size(), updated, cost);
            log.info("subScenesBackfillHandler结束, tables={}, updated={}, 耗时={}ms",
                    backfillParam.getTableNames().size(), updated, cost);
            return ReturnT.SUCCESS;
        } catch (IllegalArgumentException e) {
            XxlJobLogger.log("subScenesBackfillHandler参数错误: {}", e.getMessage());
            log.warn("subScenesBackfillHandler参数错误: {}", e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        } catch (Exception e) {
            XxlJobLogger.log("subScenesBackfillHandler异常: {}", e.getMessage());
            log.error("subScenesBackfillHandler异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }
}
