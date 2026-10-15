package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dto.PolygonNoByDeviceAndChannelDto;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yy
 */
@Mapper
public interface PolygonPatchConfigMapper extends BaseMapper<PolygonPatchConfig> {
    int isExistByPatchId(PolygonPatchVo polygonPatchVo);

    List<String> isExistLayerPatchList(@Param("list")List<PolygonPatchConfig> list, @Param("industryCode") String industryCode, @Param("tenantId") String tenantId);

    void addPolygonPatchBatch(List<PolygonPatchConfig> list);

    void updatePolygonPatchBatch(List<PolygonPatchConfig> list);

    List<PolygonPatchConfig> queryPolygonPatchList(PolygonPatchVo polygonPatchVo);

    int queryPolygonByLayerId(PolygonPatchVo polygonPatchVo);

    PolygonPatchConfig queryByLayerIdAndFeatureId(PolygonPatchVo polygonPatchVo);

    void updateDeleteFlagById(PolygonPatchVo polygonPatchVo);

    PolygonPatchConfig queryByPolygonId(String polygonId);

    PolygonPatchConfig queryPolygonPatch(PolygonPatchVo polygonPatchVo);

    List<PolygonNoByDeviceAndChannelDto> getPolygonNoByDeviceAndChannel(List<PolygonPatchConfig> list);
    
    List<PolygonPatchConfig> queryPolygonPatchShow(PolygonPatchVo polygonPatchVo);

    List<PolygonPatchConfig> queryPolygonPatchShowBatch(@Param("list")List<PolygonPatchVo> polygonPatchVoList, @Param("dataPermissions") String dataPermissions);

    Integer queryMaxNumber(PolygonPatchVo polygonPatchVo);

    Integer isExistByLayer(PolygonPatchVo polygonPatchVo);

    List<PolygonPatchConfig> queryPendingPolygonList();

    void updatePolygonPatchBatchNew(List<PolygonPatchConfig> list);

    List<String> getDeviceCodeByLayerId(@Param("list")List<String> layerIds, @Param("industryCode") String industryCode, @Param("tenantId") String tenantId);
}
