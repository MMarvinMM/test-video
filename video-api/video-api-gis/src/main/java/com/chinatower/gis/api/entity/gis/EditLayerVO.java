package com.chinatower.gis.api.entity.gis;

import lombok.Data;

@Data
public class EditLayerVO {
    /**
     * 原图层名称
     */
    private String layerName;
    /**
     * 备份图层名
     */
    private String bakLayerName;
}
