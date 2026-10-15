package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 告警扩展字段
 * @data 创建时间：2024/5/9 9:23
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAlarmExtendFieldsDTO {

    /**
     * 字段
     */
    private String code;
    /**
     * 字段描述
     */
    private String name;
    /**
     * 字段值
     */
    private Object value;
}
