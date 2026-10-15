package com.chinatower.receive.model;

import lombok.Data;

/**
 * 告警分级 AI 研判回调入参
 */
@Data
public class AiJudgeCallbackReq {

    /**
     * 是否研判成功
     */
    private Boolean success;

    /**
     * 告警工单号
     */
    private String alarm_id;

    /**
     * 风险等级（支持 1/2/3 或 高/中/低）
     */
    private Object risk_level;

    /**
     * 研判依据
     */
    private String judgment_basis;

    /**
     * 场景编码（Dify 返回中文枚举：烟+明火 / 单独烟 / 单独明火 / 未识别）
     */
    private String sceneCode;

    /**
     * 周边环境
     */
    private String surroundings;

}
