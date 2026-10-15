package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zl
 */
@Data
public class GridKeeperInfoParam extends BaseDataScopeEntity implements Serializable {
    /**
     * 网格员id
     * @required true
     */
    private Long keeperId;
}
