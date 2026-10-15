package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgAndSceSceVO {
    /** 场景编码 */
    String sceneId;
    /** 场景名称 */
    String sceneName;
    /** 原始告警子类型编码 */
    String typeValue;
    /** 客户编码 */
    String tenantId;
    /** 行业编码 */
    String industryCode;
    /** 算法配置信息 */
    List<AlgAndSceConfigVO> configList;
}
