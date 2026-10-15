package com.chinatower.gis.api.entity;

import lombok.Data;

import java.util.List;

/**
 * @desription: 删除图层
 * @data 创建时间：2023/8/13 13:02
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DeleteLayerReq {

    /**
    * 图层ID
    * @required false
    */
    private String layerId;
    /**
     * 批量删除
     */
    private List<String> layerIds;
}
