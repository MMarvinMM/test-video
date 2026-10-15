package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LayerTreeVO implements Serializable {

    /**
     * 图层类型名称
     */
    private String layerTypeName;

    /**
     * 图层信息
     */
    private List<LayerInfoVO> layerInfoList;
}
