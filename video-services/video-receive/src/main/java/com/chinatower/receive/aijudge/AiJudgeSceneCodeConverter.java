package com.chinatower.receive.aijudge;

import org.springframework.util.StringUtils;

/**
 * Dify 回调 sceneCode 中文枚举 → 库内 scene_code 映射
 * <ul>
 *   <li>烟+火 → smokeFire</li>
 *   <li>单独烟 → smoke</li>
 *   <li>单独明火 → fire</li>
 *   <li>未识别 → 不映射（返回 null）</li>
 * </ul>
 */
public final class AiJudgeSceneCodeConverter {

    public static final String RAW_SMOKE_AND_FIRE = "烟+火";
    public static final String RAW_SMOKE_ONLY = "单独烟";
    public static final String RAW_FIRE_ONLY = "单独火";
    public static final String RAW_UNRECOGNIZED = "未识别";

    public static final String CODE_SMOKE_FIRE = "smokeFire";
    public static final String CODE_SMOKE = "smoke";
    public static final String CODE_FIRE = "fire";

    private AiJudgeSceneCodeConverter() {
    }

    /**
     * 将回调中文枚举转为库内编码；未识别或未知值返回 null（不落库）
     */
    public static String toStorageCode(String sceneCodeRaw) {
        if (!StringUtils.hasText(sceneCodeRaw)) {
            return null;
        }
        String raw = sceneCodeRaw.trim();
        if (RAW_SMOKE_AND_FIRE.equals(raw)) {
            return CODE_SMOKE_FIRE;
        }
        if (RAW_SMOKE_ONLY.equals(raw)) {
            return CODE_SMOKE;
        }
        if (RAW_FIRE_ONLY.equals(raw)) {
            return CODE_FIRE;
        }
        // 未识别及其他未知值：不做映射
        return null;
    }
}
