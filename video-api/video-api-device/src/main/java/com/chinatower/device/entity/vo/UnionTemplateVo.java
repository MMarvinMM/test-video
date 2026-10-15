package com.chinatower.device.entity.vo;

import lombok.Data;

/**
 * 告警区域关联模板实体类
 */
@Data
public class UnionTemplateVo {

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 告警模板状态 1-开启 0-关闭
     */
    private String templateStatus;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;
}
