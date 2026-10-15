package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class FixedTargetRecord {

    /**
     * 记录列表
     */
    private List<FixedTargetInfo> records;

    /**
     * 数据总数
     */
    private Long total;

    /**
     * 每页显示条数
     */
    private Integer size;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 总页数
     */
    private Long pages;
}
