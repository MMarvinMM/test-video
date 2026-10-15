package com.chinatower.bdm.modules.layer.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 图层类型模式管理
 * </p>
 *
 * @author ch
 * @since 2023-04-17
 */
@Data
@ApiModel(value="LayerMode对象", description="图层类型模式管理")
public class LayerMode  {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "0-列表模式 1-常规地址 2-其他")
    private Integer modeValue;


}
