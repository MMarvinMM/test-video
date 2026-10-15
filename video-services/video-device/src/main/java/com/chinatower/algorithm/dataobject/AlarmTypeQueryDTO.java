package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 告警类型查询请求DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class AlarmTypeQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警名称（可选）
     */
    private String alarmTypeName;

    private String algorithmCode;
}

