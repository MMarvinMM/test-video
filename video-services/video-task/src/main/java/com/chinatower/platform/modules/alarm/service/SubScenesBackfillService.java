package com.chinatower.platform.modules.alarm.service;

import com.chinatower.platform.modules.alarm.dao.SubScenesBackfillMapper;
import com.chinatower.platform.modules.alarm.entity.SubScenesBackfillRow;
import com.chinatower.platform.modules.alarm.task.SubScenesBackfillParam;
import com.chinatower.platform.modules.alarm.util.subscenes.SubScenesExtractUtil;
import com.chinatower.platform.mongo.InterceptMongoMapper;
import com.chinatower.platform.mongo.OperateMongoMapper;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 历史月表 sub_scenes 刷数：游标批扫 + 提取 + 幂等更新，并同步运营/拦截 Mongo。
 * <p>非大模型 / 解析失败 / 无实际触发：写入 null（不按告警类型兜底），不拖垮整月任务。</p>
 * <p>月表已正确时仍同步 Mongo，纠正运营列表 Mongo 路径上误写的场景主类型。</p>
 */
@Slf4j
@Service
public class SubScenesBackfillService {

    @Autowired
    private SubScenesBackfillMapper subScenesBackfillMapper;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    /**
     * 按参数刷完整张月表（可从 lastId 续跑）。
     *
     * @return 本轮 MySQL 更新成功行数（Mongo 同步不计入）
     */
    public int backfill(SubScenesBackfillParam param) {
        String tableName = param.getTableName();
        int batchSize = param.getBatchSize();
        String lastId = param.getLastId() == null ? "" : param.getLastId();

        int updated = 0;
        int scanned = 0;
        int batches = 0;
        log.info("subScenesBackfill start, table={}, batchSize={}, lastId={}", tableName, batchSize, lastId);
        XxlJobLogger.log("subScenesBackfill start, table={}, batchSize={}, lastId={}",
                tableName, batchSize, lastId);

        while (true) {
            List<SubScenesBackfillRow> rows =
                    subScenesBackfillMapper.selectBatch(tableName, lastId, batchSize);
            if (rows == null || rows.isEmpty()) {
                break;
            }
            batches++;
            for (SubScenesBackfillRow row : rows) {
                scanned++;
                lastId = row.getWarningOrderId();
                updated += processOne(tableName, row);
            }
            // 不足一批说明已扫到末尾
            if (rows.size() < batchSize) {
                break;
            }
        }

        log.info("subScenesBackfill done, table={}, batches={}, scanned={}, updated={}, lastId={}",
                tableName, batches, scanned, updated, lastId);
        XxlJobLogger.log("subScenesBackfill done, table={}, batches={}, scanned={}, updated={}, lastId={}",
                tableName, batches, scanned, updated, lastId);
        return updated;
    }

    /**
     * 单行：提取 → MySQL 幂等更新 → 始终同步 Operate/Intercept Mongo。
     * 异常或无需兜底时 target 为 null。
     */
    private int processOne(String tableName, SubScenesBackfillRow row) {
        if (row == null || StringUtils.isBlank(row.getWarningOrderId())) {
            return 0;
        }
        String target;
        try {
            target = SubScenesExtractUtil.extractSubScenes(
                    row.getWarningTypeId(), row.getExtraField(), null);
        } catch (Exception e) {
            // 保证刷数不中断：异常行置空，不按告警类型兜底
            log.warn("extractSubScenes unexpected fail, orderId={}, typeId={}",
                    row.getWarningOrderId(), row.getWarningTypeId(), e);
            target = null;
        }

        int mysqlUpdated = 0;
        // Java 侧先跳过已相等，减少无谓 UPDATE；但仍须 syncMongoSubScenes
        if (!Objects.equals(normalizeJson(row.getSubScenes()), normalizeJson(target))) {
            try {
                mysqlUpdated = subScenesBackfillMapper.updateSubScenes(
                        tableName, row.getWarningOrderId(), target);
            } catch (Exception e) {
                log.error("updateSubScenes fail, table={}, orderId={}, target={}",
                        tableName, row.getWarningOrderId(), target, e);
                XxlJobLogger.log("updateSubScenes fail, orderId={}, err={}",
                        row.getWarningOrderId(), e.getMessage());
            }
        }

        // 月表已正确时仍同步 Mongo，纠正运营列表误写的场景 UUID/主类型
        syncMongoSubScenes(row.getWarningOrderId(), target);
        return mysqlUpdated;
    }

    /**
     * 将月表目标 sub_scenes 同步到运营研判 / 拦截 Mongo（精确对齐，空则 unset）。
     */
    private void syncMongoSubScenes(String warningOrderId, String targetJson) {
        try {
            operateMongoMapper.syncSubScenes(warningOrderId, targetJson);
        } catch (Exception e) {
            log.warn("syncOperateMongo sub_scenes fail, orderId={}", warningOrderId, e);
            XxlJobLogger.log("syncOperateMongo fail, orderId={}, err={}",
                    warningOrderId, e.getMessage());
        }
        try {
            interceptMongoMapper.syncSubScenes(warningOrderId, targetJson);
        } catch (Exception e) {
            log.warn("syncInterceptMongo sub_scenes fail, orderId={}", warningOrderId, e);
            XxlJobLogger.log("syncInterceptMongo fail, orderId={}, err={}",
                    warningOrderId, e.getMessage());
        }
    }

    /** 去掉空白便于比较 CAST(JSON AS CHAR) 与 Fastjson 输出 */
    private static String normalizeJson(String json) {
        if (json == null) {
            return null;
        }
        return json.trim();
    }
}
