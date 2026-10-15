package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 周边网格员搜索入参
 * @author zl
 */
@Data
public class KeeperTreeSelectParam extends BaseDataScopeEntity implements Serializable {



    /**
     * 类型 0 网格-网格长-网格员   1 网格-网格员&&网格长
     * @required true
     */
    @NotBlank(message = "树结构类型不能为空")
    private String type;


}
