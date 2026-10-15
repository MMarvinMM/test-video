package com.chinatower.gis.api.entity.layer;

import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/11/3 9:50
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeometrysReq {

    /**
     * 图层IDs
     */
    private List<String> layerIds;
}
