package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 深林图层小班详情
 * @data 创建时间：2023/12/5 15:04
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ForestLayerXmInfoReq {
    /**
     * 图斑ID
     */
    private String layerId;
    /**
     * 图斑ID
     */
    private Integer gid;
    /**
     * 标识码
     */
    private String bsm;
}
