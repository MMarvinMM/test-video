package com.chinatower.algorithm.dataobject;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 场景告警树状查询DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneAlarmQueryDTO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 算法编码
     */
    private String algorithmCode;
}

