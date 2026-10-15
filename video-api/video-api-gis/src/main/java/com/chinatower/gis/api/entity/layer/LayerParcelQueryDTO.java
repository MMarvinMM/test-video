package com.chinatower.gis.api.entity.layer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @desription: 地块查询
 * @data 创建时间：2024/1/23 16:22
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerParcelQueryDTO extends GetFeaturesByFilterReqEx{

    /**
     * 字段筛选条件
     */
    private List<LayerParcelQueryFiltersDTO> filters = new ArrayList<>();

    /**
     * 空间查询类型0-相交 1-包含
     */
    private int spaceType;
}
