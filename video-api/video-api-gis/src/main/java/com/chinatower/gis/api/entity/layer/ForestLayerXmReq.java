package com.chinatower.gis.api.entity.layer;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @desription: 深林图层 小班列表
 * @data 创建时间：2023/12/5 10:12
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ForestLayerXmReq extends PageDTO {

    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 小班
     */
    private String xiaoBan;

    /**
     * 优势树(灌)种
     */
    private String youShiSz;

    /**
     * 地类
     */
    private String diLei;

    /**
     * 字段筛选条件
     */
    private List<LayerParcelQueryFiltersDTO> filters = new ArrayList<>();
}
