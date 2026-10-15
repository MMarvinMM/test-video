package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * @author zl
 */
@Data
public class InspctionConfStatusParam extends BaseDataScopeEntity {
    private String confId;
    private Integer deleteFlag;
    private String modifier;
}
