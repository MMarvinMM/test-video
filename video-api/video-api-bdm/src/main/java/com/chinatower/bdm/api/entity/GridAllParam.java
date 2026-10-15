package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zl
 */
@Data
public class GridAllParam extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 部门编码
     */
    private String deptIds;
    private Boolean isChild=false;
}
