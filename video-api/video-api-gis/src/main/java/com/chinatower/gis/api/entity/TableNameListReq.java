package com.chinatower.gis.api.entity;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2024/5/8 9:31
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TableNameListReq {
    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 标识码
     */
    private String bsm;
    /**
     * 分页数据
     */
    private Integer pageNum;
    /**
     * 分页数据
     */
    private Integer pageSize;
}
