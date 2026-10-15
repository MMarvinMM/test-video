package com.chinatower.algorithm.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 告警类型VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警类型ID
     */
    private Long alarmTypeId;

    /**
     * 类型值
     */
    private String typeValue;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 图标
     */
    private String icon;
}

