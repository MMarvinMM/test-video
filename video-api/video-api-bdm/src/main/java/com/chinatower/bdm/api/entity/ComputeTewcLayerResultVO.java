package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @desription: 探而未采图层计算结果
 * @data 创建时间：2024/8/9 9:28
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ComputeTewcLayerResultVO {
    /**
     * 探矿权
     */
    private String tkq;
    /**
     * 采矿权
     */
    private String ckq;
    /**
     * 探而未采
     */
    private String tewc;
    /**
     * 状态 1-正常 5失败
     */
    private Integer status;
    /**
     * 状态 2-通知
     */
    private Integer type;
    /**
     * 租户 ID
     */
    private String tenantId;
    /**
     * 行业
     */
    private String industryCode;
    /**
     * 简称
     */
    private String abbreviation;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 图层命名空间
     */
    private String layerNameSpace;
    /**
     * 图层ID
     */
    private String layerId;
}
