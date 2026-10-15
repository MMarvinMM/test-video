package com.chinatower.platform.modules.alarm.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 历史 sub_scenes 刷数 Job 参数。
 * <p>
 * 单表：{"tableName":"vm_warning_order_t_202608","batchSize":2000,"lastId":""}<br>
 * 多表：{"tableNames":["vm_warning_order_t_202601","vm_warning_order_t_202609"],"batchSize":2000}
 * </p>
 */
public class SubScenesBackfillParam {

    /** 默认批大小 */
    public static final int DEFAULT_BATCH_SIZE = 2000;

    /** 仅允许告警月表名，防止 ${tableName} 注入 */
    private static final Pattern TABLE_NAME_PATTERN =
            Pattern.compile("^vm_warning_order_t_\\d{6}$");

    /** 兼容旧字段：取 tableNames 第一张 */
    private String tableName;
    private List<String> tableNames = Collections.emptyList();
    private int batchSize = DEFAULT_BATCH_SIZE;
    private String lastId = "";

    public String getTableName() {
        return tableName;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getLastId() {
        return lastId;
    }

    /**
     * 多表时忽略 lastId（各表从头扫）；仅单表续跑时返回原 lastId。
     */
    public String resolveLastIdForTable(String table) {
        if (tableNames != null && tableNames.size() == 1
                && tableNames.get(0).equals(table)) {
            return lastId == null ? "" : lastId;
        }
        return "";
    }

    /**
     * 复制为单表参数，供 Job 逐表调用 Service。
     */
    public SubScenesBackfillParam copyForTable(String singleTable) {
        SubScenesBackfillParam one = new SubScenesBackfillParam();
        one.tableName = singleTable;
        one.tableNames = Collections.singletonList(singleTable);
        one.batchSize = this.batchSize;
        one.lastId = resolveLastIdForTable(singleTable);
        return one;
    }

    /**
     * 解析 XXL 入参 JSON；tableName 或 tableNames 至少填一个，表名须匹配月表命名。
     */
    public static SubScenesBackfillParam parse(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            throw new IllegalArgumentException("tableName/tableNames 必填，param 示例:"
                    + "{\"tableNames\":[\"vm_warning_order_t_202601\",\"vm_warning_order_t_202609\"]}");
        }
        JSONObject json;
        try {
            json = JSON.parseObject(paramJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("param 不是合法 JSON: " + paramJson, e);
        }
        if (json == null) {
            throw new IllegalArgumentException("tableName/tableNames 必填");
        }

        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        String single = json.getString("tableName");
        if (StringUtils.isNotBlank(single)) {
            ordered.add(requireSafeTableName(single.trim()));
        }
        JSONArray arr = json.getJSONArray("tableNames");
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                String name = arr.getString(i);
                if (StringUtils.isBlank(name)) {
                    throw new IllegalArgumentException("tableNames 含空表名");
                }
                ordered.add(requireSafeTableName(name.trim()));
            }
        }
        if (ordered.isEmpty()) {
            throw new IllegalArgumentException("tableName/tableNames 必填");
        }

        SubScenesBackfillParam param = new SubScenesBackfillParam();
        param.tableNames = Collections.unmodifiableList(new ArrayList<>(ordered));
        param.tableName = param.tableNames.get(0);
        Integer batchSize = json.getInteger("batchSize");
        if (batchSize != null && batchSize > 0) {
            param.batchSize = batchSize;
        }
        String lastId = json.getString("lastId");
        param.lastId = lastId == null ? "" : lastId;
        return param;
    }

    private static String requireSafeTableName(String tableName) {
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException(
                    "tableName 非法，须匹配 vm_warning_order_t_YYYYMM，实际=" + tableName);
        }
        return tableName;
    }
}
