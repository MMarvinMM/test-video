package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 计算探而未采图层
 * @data 创建时间：2024/4/26 14:03
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MineralsLayerReq {

    /**
     * 探 图层名称
     */
    private String exploreLayerName;

    /**
     * 采 图层名称
     */
    private String collectingLayerName;

    /**
     * 租户 ID
     */
    private String tenantId;
    /**
     * 行业
     */
    private String industryCode;
    /**
     * 创建人
     */
    private String createBy;

    /**
     * 次数
     */
    private int count;
}
