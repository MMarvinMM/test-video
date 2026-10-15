package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @desription: 探而未采
 * @data 创建时间：2024/4/25 13:47
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MineralsLayerReqVO {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 行业
     */
    private String industryCode;
    /**
     * 探矿权图层
     */
    private String tkq;
    /**
     * 采矿权图层
     */
    private String ckq;
    /**
     * 用户名称
     */
    private String userName;

    private String nodeCode;
    /**
     * 版本号
     */
    private String appVerCode;
}
