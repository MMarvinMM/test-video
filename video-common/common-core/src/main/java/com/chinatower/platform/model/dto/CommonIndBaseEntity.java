package com.chinatower.platform.model.dto;

import com.chinatower.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 行业应用版本基类
 * @author: zheng.yan
 * @date: 2022/3/25
 * @version:
 */
@Data
public class CommonIndBaseEntity extends BaseEntity {
    /**
     * 行业版本编码
     */
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;
}
