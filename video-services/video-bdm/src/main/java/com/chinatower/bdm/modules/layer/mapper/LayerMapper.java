package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.layerTree;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.platform.annotation.DataPermissions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 图层管理 Mapper 接口
 * </p>
 *
 * @author ch
 * @since 2023-04-07
 */
public interface LayerMapper {

    Layer selectById(@Param("id") String id);

    /**
     * @param tenantId
     * @param industryCode
     * @param customName
     * @return 图层名称数量
     */
    Integer selectCustomNameCount(@Param("tenantId") String tenantId,
                                  @Param("industryCode") String industryCode,
                                  @Param("customName") String customName,
                                  @Param("category") String category);

    /**
     * @param tenantId
     * @param industryCode
     * @param layerTypeId
     * @return 图层类型数量
     */
    Integer selectLayerTypeIdCount(@Param("tenantId") String tenantId,
                                   @Param("industryCode") String industryCode,
                                   @Param("layerTypeId") String layerTypeId);

    /**
     * @param layer
     * @return 新增
     */
    Integer insert(Layer layer);

    int editLayer(String layerId,String newId,String bakLayerName);

    Integer bakMaxLayerNameNumber(String layerName);

    /**
     * @param layer
     * @return 修改
     */
    Integer updateById(Layer layer);

    /**
     * @param dto
     * @return 图层列表
     */
    List<LayerResponseDTO> list(@Param("dto") LayerQueryDTO dto, @Param("orderBy") String orderBy, @Param("acceptNode") String acceptNode);

    /**
     * SDK 图层列表
     */
    List<LayerResponseDTO> baseLayerList(@Param("dto") BaseLayerListQueryDTO dto);

    /**
     * 租户和行业下全部图层面积和数量
     */
    @DataPermissions(layerColumn = "l.id")
    LayerCountAndAreaDTO abbreviationLayersAreaAndCount(@Param("dto") LayerQueryDTO dto, @Param("acceptNode") String acceptNode);
    /**
     * 租户和行业下全部图层名称
     */
    @DataPermissions(layerColumn = "l.id")
    List<String> abbreviationLayerNames(@Param("dto") LayerQueryDTO dto, @Param("acceptNode") String acceptNode);

    /**
     * @param ids
     * @return 根据类型查询图层
     */
    List<Layer> selectByTypeIds(@Param("ids") List<String> ids);

    /**
     * @param ids
     * @return 删除图层
     */
    Integer deleteBatchIds(@Param("ids") List<String> ids);

    List<PolygonAttrDictVO> getPolygonAttrDict(PolygonAttrDictVO polygonAttrDictVO);

    List<LayerTypeAttrTilterVO> getLayerTypeAttrFilter(LayerTypeAttrTilterVO layerTypeAttrTilterVO);

    List<LayerAreaVO> getLayerArea(LayerAreaVO layerAreaVO);

    InspectionRouteRelLayerSdkVO getDeviceByPolygon(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);

    List<InspectionRouteRelLayerSdkVO> getPolygonLayers(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);
    List<InspectionRouteRelLayerSdkVO> getPolygonFeatures(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);


    Layer selectByTypeIdAndNestFlag(LayerQueryDTO layerQueryDTO);

    void updateNestFlag(@Param("id") String id, @Param("nestFlag") int nestFlag, @Param("username") String username);

    int getCustomNameCount(Layer layer);

    List<Layer> selectByTypeIdsAndTenant(@Param("ids") List<String> ids, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("nodeCode") String nodeCode);

    List<String> selectNestTypeIdByIds(List<String> ids);

    void updateNestByLayerTypeIds(@Param("typeIds")List<String> typeIds, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<String> getNoNestByLayerTypeIds(@Param("typeIds")List<String> typeIds, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<LayerAreaCountVO> layerAreaCount(@Param("ids") List<String> ids);

    Integer mineralsLayerDelete(@Param("tenantId") String tenantId,
                                @Param("industryCode") String industryCode);

    /**
     * 探而未采图层类型ID
     */
    String mineralsLayerTypeId(@Param("tenantId") String tenantId,
                               @Param("industryCode") String industryCode);

    /**
     * 探而未采图层ID
     */
    @DataPermissions(layerColumn = "l.id")
    String mineralsLayerId(@Param("dto") LayerQueryDTO dto);

    /**
     * 获得默认图层名称
     */
    String getAbbreviationLayerName(@Param("tenantId") String tenantId,
                                    @Param("industryCode") String industryCode,
                                    @Param("abbreviation") String abbreviation);

    /**
     * 图层是否存在
     */
    @DataPermissions(layerColumn = "l.id")
    Integer layerNameCount(@Param("dto") LayerQueryDTO dto,
                                    @Param("layerNames") List<String> layerNames);

    List<Layer> selectNestLayerByTypeIds(List<String> typeIds);

    List<Layer> findLayerByLayerIds(List<String> ids);

    /**
     * 根据ID查询图层
     */
    List<Layer> selectByLayerIds(@Param("ids") List<String> ids);

    List<layerTree> layerList(LayerQueryDTO dto);

    List<layerTree> layerTypeList(LayerQueryDTO dto);

    void addDataPermissions(Layer layer);

    void delDataPermissions(@Param("ids") List<String> ids);

    void deletePolygonPatchByLayerIds(List<String> ids);

    void deletePolygonPatchAlgoAreaByLayerIds(List<String> ids);

    void deleteInspectionRouteFenceByLayerIds(List<String> ids);

    List<LayerCountRes> getLayerCount(LayerQueryDTO dto);
}
