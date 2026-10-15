package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class GridKeeperLayerParam extends GridKeeperVO implements Serializable {

    /**
     * 网格id
     */
    private Long gridId;

    /**
     * 地市id
     */
    private String cityId;

    /**
     * 状态
     */
    private String status;

    /**
     * 关键字查询，APP用
     */
    private String keyword;
    /**
     * 网格层级编码
     */
    private String gridLevelCode;
    private static final long serialVersionUID = 1L;
}
