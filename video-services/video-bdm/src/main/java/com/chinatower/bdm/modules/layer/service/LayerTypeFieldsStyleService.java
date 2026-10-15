package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.modules.layer.domain.LayerTypeStyleVo;

import java.util.List;

public interface LayerTypeFieldsStyleService {

    void addOrModify(LayerTypeStyleVo layerTypeStyle);

    LayerTypeStyleVo listByLayerTypeId(LayerTypeStyleVo layerTypeStyle);

    void delStyle(LayerTypeStyleVo listByLayerType);
}
