package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @desription: geoserver 相关数据
 * @data 创建时间：2024/7/29 17:22
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeoVO {
    /**
     * 样式名称
     */
    private String styleName;
    /**
     * 图层名称
     */
    private String layerName;
    /**
     * 存储仓库名称
     */
    private String storeName;

    /**
     * 是否查询样式命名空间
     */
    private Boolean workspaceFilterFlag;
}
