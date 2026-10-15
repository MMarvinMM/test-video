package com.chinatower.platform.controller;

import com.chinatower.platform.consumer.item.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MongoDB脚本安全重建器
 */
@Slf4j
@Component
public class MongoScriptSecurityRebuilder {

    // 允许的命令
    private static final Set<String> ALLOWED_COMMANDS;

    // 真正危险的操作符（必须禁止）
    private static final Set<String> DANGEROUS_OPS;

    // 允许的集合（只允许 vm_video_preset_snapshot）
    private static final Set<String> ALLOWED_COLLECTIONS;

    // 查询限制
    private static final int MAX_LIMIT = 10000;
    private static final int DEFAULT_LIMIT = 1000;
    private static final int MAX_SKIP = 50000;

    static {
        // 初始化允许的命令
        Set<String> commands = new HashSet<>();
        commands.add(Constants.FIND);
        commands.add(Constants.AGGREGATE);
        commands.add("count");
        ALLOWED_COMMANDS = Collections.unmodifiableSet(commands);

        // 初始化危险操作符
        Set<String> dangerous = new HashSet<>();
        dangerous.add("$where");
        dangerous.add("$eval");
        dangerous.add("$function");
        dangerous.add("$accumulator");
        dangerous.add("$out");
        dangerous.add("$merge");
        dangerous.add("$redact");
        DANGEROUS_OPS = Collections.unmodifiableSet(dangerous);

        // 初始化允许的集合
        Set<String> collections = new HashSet<>();
        collections.add("vm_video_preset_snapshot");
        collections.add("vm_video_task_snapshot");
        ALLOWED_COLLECTIONS = Collections.unmodifiableSet(collections);
    }

    /**
     * 安全重建入口
     */
    public String rebuildSafeScript(String originalScript) throws SecurityException {
        if (StringUtils.isBlank(originalScript)) {
            throw new SecurityException("脚本不能为空");
        }

        try {
            // 1. 解析原始脚本
            Document originalDoc = Document.parse(originalScript);

            // 2. 基础安全校验
            validateBasicSecurity(originalDoc);

            // 3. 重建安全脚本
            Document safeDoc = rebuildDocumentSafely(originalDoc);

            return safeDoc.toJson();

        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            log.error("脚本处理异常", e);
            throw new SecurityException("脚本格式错误: " + e.getMessage());
        }
    }

    /**
     * 基础安全校验
     */
    private void validateBasicSecurity(Document doc) throws SecurityException {
        // 1. 检查命令类型
        String command = null;
        String collection = null;

        for (String key : doc.keySet()) {
            if (!key.startsWith("$") && ALLOWED_COMMANDS.contains(key)) {
                command = key;
                Object collObj = doc.get(key);
                if (collObj instanceof String) {
                    collection = (String) collObj;
                }
                break;
            }
        }

        if (command == null) {
            throw new SecurityException("未找到允许的命令");
        }

        // 2. 检查集合
        if (collection == null || !ALLOWED_COLLECTIONS.contains(collection)) {
            throw new SecurityException("只允许访问初始化允许的相关集合");
        }

        // 3. 检查危险操作符
        checkForDangerousContent(doc);
    }

    /**
     * 检查危险内容
     */
    private void checkForDangerousContent(Object obj) throws SecurityException {
        if (obj instanceof Document) {
            Document doc = (Document) obj;
            for (String key : doc.keySet()) {
                // 检查危险操作符
                if (DANGEROUS_OPS.contains(key)) {
                    throw new SecurityException("包含危险操作符: " + key);
                }

                // 递归检查
                checkForDangerousContent(doc.get(key));
            }
        } else if (obj instanceof List) {
            for (Object item : (List<?>) obj) {
                checkForDangerousContent(item);
            }
        }
    }

    /**
     * 安全重建文档
     */
    private Document rebuildDocumentSafely(Document originalDoc) {
        Document safeDoc = new Document();

        // 复制所有非命令字段
        for (String key : originalDoc.keySet()) {
            if (!ALLOWED_COMMANDS.contains(key) && !key.startsWith("$")) {
                safeDoc.put(key, originalDoc.get(key));
            }
        }

        // 处理命令
        for (String key : originalDoc.keySet()) {
            if (ALLOWED_COMMANDS.contains(key)) {
                // 集合名称
                safeDoc.put(key, originalDoc.get(key));

                // 处理特定命令的参数
                if (Constants.FIND.equals(key)) {
                    applyFindLimits(originalDoc, safeDoc);
                } else if (Constants.AGGREGATE.equals(key)) {
                    applyAggregateLimits(originalDoc, safeDoc);
                }
                break; // 只处理第一个命令
            }
        }

        return safeDoc;
    }

    /**
     * 对 find 命令应用限制
     */
    private void applyFindLimits(Document originalDoc, Document safeDoc) {
        // 复制filter
        if (originalDoc.containsKey(Constants.FILTER)) {
            safeDoc.put(Constants.FILTER, originalDoc.get(Constants.FILTER));
        }
        if (originalDoc.containsKey(Constants.QUERY)) {
            safeDoc.put(Constants.FILTER, originalDoc.get(Constants.QUERY));
        }

        // 复制projection
        if (originalDoc.containsKey(Constants.PROJECTION)) {
            safeDoc.put(Constants.PROJECTION, originalDoc.get(Constants.PROJECTION));
        }

        // 复制sort
        if (originalDoc.containsKey(Constants.SORT)) {
            safeDoc.put(Constants.SORT, originalDoc.get(Constants.SORT));
        }

        // 处理limit
        if (originalDoc.containsKey(Constants.LIMIT)) {
            Object limitObj = originalDoc.get(Constants.LIMIT);
            if (limitObj instanceof Number) {
                int limit = ((Number) limitObj).intValue();
                safeDoc.put(Constants.LIMIT, Math.max(1, Math.min(limit, MAX_LIMIT)));
            } else {
                safeDoc.put(Constants.LIMIT, DEFAULT_LIMIT);
            }
        } else {
            safeDoc.put(Constants.LIMIT, DEFAULT_LIMIT);
        }

        // 处理skip
        if (originalDoc.containsKey(Constants.SKIP)) {
            Object skipObj = originalDoc.get(Constants.SKIP);
            if (skipObj instanceof Number) {
                int skip = ((Number) skipObj).intValue();
                safeDoc.put(Constants.SKIP, Math.max(0, Math.min(skip, MAX_SKIP)));
            }
        }
    }

    /**
     * 对 aggregate 命令应用限制（修复版）
     */
    private void applyAggregateLimits(Document originalDoc, Document safeDoc) {
        // 确保cursor存在
        safeDoc.put("cursor", new Document());

        // 处理pipeline
        if (originalDoc.containsKey(Constants.PIPELINE)) {
            Object pipelineObj = originalDoc.get(Constants.PIPELINE);
            if (pipelineObj instanceof List) {
                List<?> originalPipeline = (List<?>) pipelineObj;
                List<Document> safePipeline = new ArrayList<>();
                boolean hasLimit = false;

                for (Object stageObj : originalPipeline) {
                    if (stageObj instanceof Document) {
                        Document stage = (Document) stageObj;
                        Document safeStage = processPipelineStage(stage);

                        if (!safeStage.isEmpty()) {
                            safePipeline.add(safeStage);

                            // 检查是否有limit阶段
                            if (safeStage.containsKey(Constants.PREFIX_LIMIT)) {
                                hasLimit = true;
                            }
                        }
                    }
                }

                // 如果没有limit阶段，添加一个
                if (!hasLimit) {
                    safePipeline.add(new Document(Constants.PREFIX_LIMIT, DEFAULT_LIMIT));
                }

                safeDoc.put(Constants.PIPELINE, safePipeline);
            }
        }
    }

    /**
     * 处理单个pipeline阶段
     */
    private Document processPipelineStage(Document stage) {
        Document safeStage = new Document();

        for (String stageKey : stage.keySet()) {
            Object stageValue = stage.get(stageKey);

            if (Constants.PREFIX_LIMIT.equals(stageKey)) {
                safeStage.put(Constants.PREFIX_LIMIT, validateLimit(stageValue));
            } else if (Constants.PREFIX_SKIP.equals(stageKey)) {
                safeStage.put(Constants.PREFIX_SKIP, validateSkip(stageValue));
            } else if ("$match".equals(stageKey) ||
                    "$project".equals(stageKey) ||
                    "$sort".equals(stageKey) ||
                    "$group".equals(stageKey) ||
                    "$unwind".equals(stageKey) ||
                    "$lookup".equals(stageKey) ||
                    "$addFields".equals(stageKey) ||
                    "$count".equals(stageKey)) {
                // 这些阶段直接允许，但需要确保内容安全
                if (stageValue instanceof Document) {
                    safeStage.put(stageKey, ensureStageContentSafe((Document) stageValue));
                } else {
                    safeStage.put(stageKey, stageValue);
                }
            } else {
                // 其他阶段暂时不处理（可以根据需要添加）
                // 不添加，保持安全
            }
        }

        return safeStage;
    }

    /**
     * 验证limit值
     */
    private int validateLimit(Object limitObj) {
        if (limitObj instanceof Number) {
            int limit = ((Number) limitObj).intValue();
            return Math.max(1, Math.min(limit, MAX_LIMIT));
        }
        return DEFAULT_LIMIT;
    }

    /**
     * 验证skip值
     */
    private int validateSkip(Object skipObj) {
        if (skipObj instanceof Number) {
            int skip = ((Number) skipObj).intValue();
            return Math.max(0, Math.min(skip, MAX_SKIP));
        }
        return 0;
    }

    /**
     * 确保阶段内容安全
     */
    private Object ensureStageContentSafe(Document content) {
        Document safeContent = new Document();

        for (String key : content.keySet()) {
            Object value = content.get(key);

            // 递归处理嵌套文档
            if (value instanceof Document) {
                safeContent.put(key, ensureStageContentSafe((Document) value));
            } else if (value instanceof List) {
                List<Object> safeList = new ArrayList<>();
                for (Object item : (List<?>) value) {
                    if (item instanceof Document) {
                        safeList.add(ensureStageContentSafe((Document) item));
                    } else {
                        safeList.add(item);
                    }
                }
                safeContent.put(key, safeList);
            } else {
                safeContent.put(key, value);
            }
        }

        return safeContent;
    }
}