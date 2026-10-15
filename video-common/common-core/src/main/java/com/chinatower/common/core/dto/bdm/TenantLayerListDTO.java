package com.chinatower.common.core.dto.bdm;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @desription: 图层列表
 * @data 创建时间：2023/4/10 13:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class TenantLayerListDTO {

    /**
     * 图层列表名称
     */
    private String layerTypeName;
    /**
     * 简称
     */
    private String abbreviation;

    private Integer markFlag;
    /**
     * 类别下的图层
     */
    private List<LayerResponseDTO> layers;
}
