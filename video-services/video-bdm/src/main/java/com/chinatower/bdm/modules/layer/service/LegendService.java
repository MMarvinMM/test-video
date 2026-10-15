package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.modules.layer.domain.LayerTypeStyle;
import com.chinatower.bdm.modules.layer.domain.LayerTypeStyleModifyDeleteDto;
import com.chinatower.common.core.dto.bdm.LayerTypeStyleMappingVO;
import com.chinatower.platform.model.dto.LoginUser;

import java.util.List;
import java.util.Map;

public interface LegendService {

    /**
     * 发布图例样式
     */
    void updateLegend(LayerTypeStyle style);

    /**
     * 图例样式新增修改上传
     */
    void updateLegends(LayerTypeStyleModifyDeleteDto dto);

    /**
     * 查询图层的样式
     */
    Map<String,List<LayerTypeStyleMappingVO>> layerStyles(List<String> layerIds, LoginUser loginUser);

    /**
     * 快速生成图层样式
     */
    void quickGenStyles(String tenantId, String industryCode, String abbreviation);
}
