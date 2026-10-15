package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @desription: 探而未采
 * @data 创建时间：2024/4/25 13:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ToString
public class MineralsLayerVO {


    /**
     * 图层ID
     */
    private String layerId;

    /**
     * 状态 0-计算中 1-计算完成 2-数据变更 3-丢弃 4-图层缺失
     */
    private Integer status;
}
