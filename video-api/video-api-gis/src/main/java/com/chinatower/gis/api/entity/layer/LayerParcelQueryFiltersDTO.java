package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 地块查询条件
 * @data 创建时间：2024/1/23 17:46
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerParcelQueryFiltersDTO {

    /**
     * 字段值
     */
    private String searchKey;

    /**
     * 搜索类型 1-and 2-or
     */
    private Integer searchType;
    /**
     * 搜索值
     */
    private String searchValue;
    /**
     * 搜索类型 {@link com.chinatower.platform.enums.LayerParcelQueryValueTypeEnums}
     */
    private Integer valueType;
}
