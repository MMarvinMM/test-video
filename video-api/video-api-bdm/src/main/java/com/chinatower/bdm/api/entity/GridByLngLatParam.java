package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zl
 */
@Data
public class GridByLngLatParam extends BaseDataScopeEntity {
    /**
     * 经度
     * @required true
     */
    private String longitude;
    /**
     * 维度
     * @required true
     */
    private String latitude;
    /**
     * 租户ID集合
     * @required true
     */
    private List<String> tenantIdList;

    /**
     * 租户、行业集合
     */
    private List<BaseIndustryEntity> tenantList;
}
