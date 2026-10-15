package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import com.chinatower.common.core.dto.bdm.LayerPolygonAddDTO;
import com.chinatower.common.core.dto.bdm.LayerPolygonDTO;
import com.chinatower.common.core.dto.bdm.LayerPolygonQueryDTO;
import com.chinatower.common.entity.Result;

import java.util.List;

/**
 * <p>
 * 图层图斑管理 服务类
 * </p>
 *
 * @author ch
 * @since 2023-04-10
 */
public interface ILayerPolygonService {

    /**
     *
     * @param dto
     * @return 图斑列表
     */
    List<LayerPolygon> list(LayerPolygonQueryDTO dto);

    /**
     *
     * @param layerId
     * @param featureId
     * @return
     */
    LayerPolygonDTO info(String layerId, String featureId);
    /**
     *
     * @param layerId
     * @return 图层下图斑数量
     */
    Integer getLayerPolygonCount(String layerId);
    /**
     *
     * @param dto
     * @return 新增图斑
     */
    Result<String> addLayerPolygon(LayerPolygonAddDTO dto);

}
