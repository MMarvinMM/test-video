package com.chinatower.receive.aijudge;

import lombok.Data;

/**
 * 智能体扩展字段（回调落库 JSON，驼峰）
 */
@Data
public class AiAgentExtendVO {

    /** 风险等级 */
    private String riskLevel;

    /** 湿度 */
    private String humidity;

    /** 温度 */
    private String temperature;

    /** 风速 */
    private String windSpeed;

    /** 风向 */
    private String windDir;

    /** 火险等级 */
    private Integer fireLevel;

    /** 周边环境 */
    private String surroundings;

    /** 研判依据 */
    private String judgmentBasis;
}
