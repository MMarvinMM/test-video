package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.layer.domain.layerTree;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.enums.SysLayerCategoryEnums;
import com.chinatower.common.entity.Result;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 图层管理 服务类
 * </p>
 *
 * @author ch
 * @since 2023-04-07
 */
public interface ILayerService {


    /**
     *
     * @return 图层列表
     */
    List<LayerResponseDTO> list(LayerQueryDTO dto);

    /**
     * SDK 图层列表
     */
    List<LayerResponseDTO> baseLayerList(BaseLayerListQueryDTO dto);

    /**
     *
     * @param id
     * @return 图层详情
     */
    Result<LayerInfoDTO> info(String id);

    /**
     * 查询图层详情
     * @param id
     * @param category {@link SysLayerCategoryEnums}
     * @return
     */
    LayerResponseDTO selectLayerById(String id, String category);
    /**
     *
     * @param dto
     * @return 修改新增图层
     */
    Result<String> addLayer(LayerAddDTO dto);
    /**
     *
     * @param vo
     * @return 删除类别
     */
    Result<String> deleted(DeleteDTO vo);

    Result<List<PolygonAttrDictVO>> getPolygonAttrDict(PolygonAttrDictVO polygonAttrDictVO);

    Result<Map<String, List<LayerTypeAttrTilterVO>>> getLayerTypeAttrFilter(LayerTypeAttrTilterVO layerTypeAttrTilterVO);

    Result<List<LayerAreaVO>> getLayerArea(LayerAreaVO layerAreaVO);

    Result<InspectionRouteRelLayerSdkVO> getDeviceByPolygon(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);

    Result<List<InspectionRouteRelLayerSdkVO>> getPolygonLayers(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);
    Result<List<InspectionRouteRelLayerSdkVO>> getPolygonFeatures(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);


    void updateNestFlag(LayerQueryDTO layerQueryDTO);

    Map<String, LayerResponseDTO> getNestList(LayerQueryDTO layerQueryDTO);

    /**
     * 查询图层面积图斑数量
     * @param layerIds 图层ID
     */
    List<LayerAreaCountVO> layerAreaCount(List<String> layerIds);

    /**
     *
     * @param code 分组统计的字段
     * @param ratioFlag 是否计算比例
     * @param abbreviation 图层类型简称
     * @return 租户 + 行业 图层类别所有数据分类面积统计
     */
    Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(String code, Integer ratioFlag, String abbreviation);

    /**
     *
     * @param abbreviation 图层类型简称
     * @return 租户 + 行业下所有对应检查的图层的面积和
     */
    Result<LayerCountAndAreaDTO> layerCountAndArea(String abbreviation);

    Map<String, List<LayerResponseDTO>> getTenantListMultiType(LayerQueryDTO dto);

    Result<List<layerTree>> layerTree(LayerQueryDTO dto);

    List<LayerCountRes> getLayerCount(LayerQueryDTO dto);
}
