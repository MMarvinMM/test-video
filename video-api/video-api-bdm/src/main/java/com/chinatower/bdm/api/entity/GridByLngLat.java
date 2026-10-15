package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GridByLngLat implements Serializable {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 网格ID
     */
    private String gridId;
    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 网格层级
     */
    private String gridLevel;

    /**
     * 用户集合
     */
    private List<String> userList;
}
