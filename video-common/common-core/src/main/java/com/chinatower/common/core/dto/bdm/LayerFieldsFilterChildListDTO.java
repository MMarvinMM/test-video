package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 图层字段筛选下拉数据
 * @data 创建时间：2024/1/23 9:39
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerFieldsFilterChildListDTO {

    /**
     * 字典ID数据
     */
    private String id;
    /**
     * 字段代码
     */
    private String code;
    /**
     * key
     */
    private String filterAttrKey;

    /**
     * value
     */
    private String filterAttrValue;
}
