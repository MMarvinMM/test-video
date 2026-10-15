package com.chinatower.bdm.modules.layer.mapper;


import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 图层图斑管理 Mapper 接口
 * </p>
 *
 * @author ch
 * @since 2023-04-10
 */
public interface LayerPolygonMapper  {

    List<LayerPolygon> selectList(LayerPolygon polygon);
    /**
     *
     * @param id
     * @return 通过图层ID删除图斑
     */
    Integer deleteLayerId(@Param("id")String id);

    /**
     *
     * @param tenantId
     * @param industryCode
     * @param featureIds
     * @return 查询条件数量
     */
    Integer selectListCount(@Param("tenantId") String tenantId,
                            @Param("industryCode") String industryCode,
                            @Param("featureIds") List<String> featureIds);

    Integer selectCount(@Param("tenantId") String tenantId,
                        @Param("industryCode") String industryCode,
                        @Param("layerId") String layerId);
    /**
     *
     * @param tenantId
     * @param industryCode
     * @param layerIds
     * @return 查询条件数量
     */
    Integer deleteLayerIds(@Param("tenantId") String tenantId,
                            @Param("industryCode") String industryCode,
                            @Param("layerIds") List<String> layerIds);

    Integer saveBatch(@Param("list") List<LayerPolygon> list);
    Integer insert(LayerPolygon layerPolygon);
}
