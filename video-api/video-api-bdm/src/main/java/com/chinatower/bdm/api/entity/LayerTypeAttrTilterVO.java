package com.chinatower.bdm.api.entity;

import lombok.Data;

@Data
public class LayerTypeAttrTilterVO {
    /**
     *  图层类别
     */
    private String abbreviation;
    /**
     *  筛选类型
     */
    private String filterType;
    /**
     *  筛选属性编号
     */
    private String filterAttrKey;
    /**
     *  筛选属性值
     */
    private String filterAttrValue;
    /**
     *  0文本  1下拉
     */
    private String showType;

    /**
     * 是否显示 0否 1是
     */
    private String isShow;
}
