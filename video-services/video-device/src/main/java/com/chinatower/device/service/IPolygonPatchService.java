package com.chinatower.device.service;

import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yy
 */
public interface IPolygonPatchService {
    List<PolygonPatchConfig> queryPolygonPatchList(PolygonPatchVo polygonPatchVo);

    List<PolygonPatchConfig> addOrUpdatePolygonPatch(List<PolygonPatchConfig> polygonPatchList, boolean isAlgorithm);

    void deletePolygonPatch(PolygonPatchVo polygonPatchVo);

    Integer isExistByPatchId(PolygonPatchVo polygonPatchVo);

    List<PolygonPatchConfig> dealPolygonPatchByAlgorithm(List<PolygonPatchConfig> polygonPatchConfig);

    Integer queryPolygonByLayerId(PolygonPatchVo polygonPatchVo);

    PolygonPatchConfig queryByLayerIdAndFeatureId(PolygonPatchVo polygonPatchVo);

    PolygonPatchConfig queryPolygonPatch(PolygonPatchVo polygonPatchVo);
    
    List<PolygonPatchConfig> queryPolygonPatchShow(PolygonPatchVo polygonPatchVo);

    List<PolygonPatchConfig> queryPolygonPatchShowBatch(PolygonPatchVo polygonPatchVoList);

    int queryMaxNumber(PolygonPatchVo polygonPatchVo);

    Integer isExistByLayer(PolygonPatchVo polygonPatchVo);

    String initPolygonLonLat();

    List<String> getDeviceCodeByLayerId(List<String> layerIds, String industryCode, String tenantId);
}
