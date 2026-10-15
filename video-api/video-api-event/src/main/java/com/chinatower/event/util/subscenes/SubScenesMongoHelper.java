package com.chinatower.event.util.subscenes;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.entity.dto.InterceptMongo;
import com.chinatower.event.entity.dto.OperateMongo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 方案 G：将 MySQL/VO 的 subScenes JSON 文本写入 Mongo sub_scenes 数组字段；
 * 并提供客户视角列表查询的下游任务过滤条件（仅匹配 sub_scenes）。
 * <p>接警写入 / 历史刷数同步：有 JSON 则原样写入；空则不写/清空，不得用场景主类型兜底覆盖下游编码。</p>
 */
public final class SubScenesMongoHelper {

    /** Mongo 字段名，与月表 sub_scenes 对齐 */
    public static final String FIELD_SUB_SCENES = "sub_scenes";

    private SubScenesMongoHelper() {
    }

    /**
     * 客户视角：subSceneIdList 仅过滤 sub_scenes（数组命中任一编码即可，多选 OR）。
     *
     * @return 无列表时返回 null，调用方不加条件
     */
    public static Criteria subScenesOnlyCriteria(List<String> subSceneIdList) {
        if (CollectionUtils.isEmpty(subSceneIdList)) {
            return null;
        }
        return Criteria.where(FIELD_SUB_SCENES).in(subSceneIdList);
    }

    /**
     * 精确解析月表/VO 的 sub_scenes JSON；空或非法返回 null（不回退 warningTypeId）。
     */
    public static List<String> parseSubScenesExact(String subScenesJson) {
        if (!StringUtils.hasText(subScenesJson)) {
            return null;
        }
        try {
            List<String> codes = JSON.parseArray(subScenesJson.trim(), String.class);
            if (CollectionUtils.isEmpty(codes)) {
                return null;
            }
            return codes;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 解析 subScenes JSON；空或非法时回退为单元素 warningTypeId（仅兼容旧调用）。
     * 新写路径 / 刷数同步请用 {@link #parseSubScenesExact(String)}。
     */
    public static List<String> parseSubScenesList(String subScenesJson, String fallbackWarningTypeId) {
        List<String> exact = parseSubScenesExact(subScenesJson);
        if (!CollectionUtils.isEmpty(exact)) {
            return exact;
        }
        if (StringUtils.hasText(fallbackWarningTypeId)) {
            return Collections.singletonList(fallbackWarningTypeId);
        }
        return null;
    }

    /**
     * 接警写入 Operate Mongo：仅当 VO.subScenes 有实际编码时写入；空则保持 null（与月表不兜底一致）。
     */
    public static void applyToOperateMongo(OperateMongo mongo, String subScenesJson, String fallbackWarningTypeId) {
        List<String> codes = parseSubScenesExact(subScenesJson);
        if (!CollectionUtils.isEmpty(codes)) {
            mongo.setSubScenes(codes);
        }
    }

    /**
     * 接警写入 Intercept Mongo：同 {@link #applyToOperateMongo}。
     */
    public static void applyToInterceptMongo(InterceptMongo mongo, String subScenesJson, String fallbackWarningTypeId) {
        List<String> codes = parseSubScenesExact(subScenesJson);
        if (!CollectionUtils.isEmpty(codes)) {
            mongo.setSubScenes(codes);
        }
    }
}
