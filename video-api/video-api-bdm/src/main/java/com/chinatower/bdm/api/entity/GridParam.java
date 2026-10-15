package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * 查询网格列表入参
 * @author zl
 */
@Data
public class GridParam extends BaseDataScopeEntity {
    private String parentId;
    private String tenantId;
    private String excludeGridId;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 条数限制
     */
    private Integer pageSize;

    private String sort;

    private String sortName;

}
