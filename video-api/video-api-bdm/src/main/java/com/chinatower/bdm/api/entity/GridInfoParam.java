package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询网格入参
 * @author zl
 */
@Data
public class GridInfoParam extends BaseDataScopeEntity implements Serializable {

    /**
     * 网格id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gridId;

}
