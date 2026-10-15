package com.chinatower.algorithm.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 告警类型项
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmTypeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警类型编码
     */
    private String alarmType;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;
}
