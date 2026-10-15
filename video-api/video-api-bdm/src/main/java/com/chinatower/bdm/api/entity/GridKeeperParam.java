package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class GridKeeperParam extends GridKeeperVO implements Serializable {
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    /**
     * 条数限制
     */
    private Integer pageSize = 10;

    private Long gridId;

    private String cityId;

    private String status;

    private String deptId;

    /**
     * 关键字查询，APP用
     */
    private String keyword;

    private static final long serialVersionUID = 1L;
}
