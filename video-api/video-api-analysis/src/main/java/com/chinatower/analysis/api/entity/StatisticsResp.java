package com.chinatower.analysis.api.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2024/5/8 15:29
 */
@Data
public class StatisticsResp {

    /**
     * 标签id
     */
    private String tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 告警数量
     */
    private String num;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 告警数量
     */
    private String realNum;

    /**
     * 告警数量
     */
    private String orderCompleteRealNum;
}
