package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmTypeVO extends BaseIndustryEntity implements Serializable {

    /**
     *  告警类型ID
     */
    private Long alarmTypeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型值
     */
    private String typeValue;

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 告警来源名称
     */
    private String alarmSourceName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 大模型算法标识 true-大模型算法
     */
    private Boolean modelAlgorithmFlag;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 大模型算法名称
     */
    private String modelTypeName;

    /**
     * 大模型算法类型  1-大模型父算法
     */
    private Integer modelAlgorithmType;
}
