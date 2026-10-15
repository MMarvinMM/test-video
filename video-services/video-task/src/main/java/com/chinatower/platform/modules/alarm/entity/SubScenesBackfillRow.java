package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

/**
 * 历史 sub_scenes 刷数扫描行。
 */
@Data
public class SubScenesBackfillRow {

    /** 工单 ID（游标） */
    private String warningOrderId;

    /** 主场景类型编码 */
    private String warningTypeId;

    /** 裁剪后的 CVLM / 普通 extra_field */
    private String extraField;

    /** 当前已落库的 sub_scenes（可能为 null） */
    private String subScenes;
}
