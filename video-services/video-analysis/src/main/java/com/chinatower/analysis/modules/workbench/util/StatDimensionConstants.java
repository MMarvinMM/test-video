package com.chinatower.analysis.modules.workbench.util;

import com.chinatower.analysis.api.entity.AnalysisVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户统计双维度：
 * <ul>
 *   <li>TYPE=告警类型（利旧，含场景）</li>
 *   <li>SUB_TASK=下游任务 + 小模型（非场景类型），不带场景维度</li>
 * </ul>
 */
public final class StatDimensionConstants {

    /** 告警类型维度（默认，含大模型场景） */
    public static final String TYPE = "TYPE";

    /**
     * 下游任务客户视角：下游任务汇总 + 小模型（原告警类型中非场景编码），排除场景。
     */
    public static final String SUB_TASK = "SUB_TASK";

    /**
     * 大屏工单状态筛选项全集（与前端全选一致）。
     * 含该全集时等价于未筛选。
     */
    public static final Set<String> ALL_ORDER_STATUS_CODES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("0", "1", "2", "4", "5", "6", "7", "9")));

    /**
     * 下游日汇总表可映射的工单状态（与 TYPE/num_day 对齐，含派单/处置/审核/验收）。
     */
    public static final Set<String> SUB_TASK_MAPPED_ORDER_STATUS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("0", "1", "2", "4", "5", "6", "7", "9")));

    /**
     * 大屏告警来源筛选项常见全集（与前端全选一致，含 12 等扩展码）。
     */
    public static final Set<String> ALL_WARNING_SOURCE_CODES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("1", "2", "3", "4", "8", "9", "10", "12")));

    private StatDimensionConstants() {
    }

    public static boolean isSubTask(String statDimension) {
        return SUB_TASK.equalsIgnoreCase(statDimension) || "sub_task".equalsIgnoreCase(statDimension);
    }

    /**
     * 大屏过滤归一：
     * <ul>
     *   <li>全选工单状态/告警来源 → 清空，与不传参一致（{@code warning_num}）</li>
     *   <li>SUB_TASK 多选：保留可映射状态 {0,1,2,4,5,6,7,9}；若去重后为空则回退全量</li>
     * </ul>
     */
    public static void normalizeScreenAllFilters(AnalysisVO analysisVO) {
        if (analysisVO == null) {
            return;
        }
        if (isFullSelection(analysisVO.getOrderStatusList(), ALL_ORDER_STATUS_CODES)) {
            analysisVO.setOrderStatusList(null);
        } else if (isSubTask(analysisVO.getStatDimension())) {
            analysisVO.setOrderStatusList(retainSubTaskMappedOrderStatus(analysisVO.getOrderStatusList()));
        }
        if (isFullSelection(analysisVO.getWarningSourceList(), ALL_WARNING_SOURCE_CODES)) {
            analysisVO.setWarningSourceList(null);
        }
    }

    /**
     * SUB_TASK：只保留下游表可映射状态；列表为空原样返回；
     * 选了状态但无一可映射时返回 null（回退 warning_num）。
     */
    static List<String> retainSubTaskMappedOrderStatus(List<String> orderStatusList) {
        if (CollectionUtils.isEmpty(orderStatusList)) {
            return orderStatusList;
        }
        LinkedHashSet<String> mapped = new LinkedHashSet<>();
        for (String status : orderStatusList) {
            if (SUB_TASK_MAPPED_ORDER_STATUS.contains(status)) {
                mapped.add(status);
            }
        }
        if (mapped.isEmpty()) {
            return null;
        }
        return new ArrayList<>(mapped);
    }

    static boolean isFullSelection(List<String> selected, Set<String> allCodes) {
        if (CollectionUtils.isEmpty(selected) || allCodes == null || allCodes.isEmpty()) {
            return false;
        }
        return new HashSet<>(selected).containsAll(allCodes);
    }
}
