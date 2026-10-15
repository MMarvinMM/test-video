package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.bdm.modules.layer.domain.LayerTypeStyle;
import com.chinatower.bdm.modules.layer.domain.LayerTypeStyleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LayerTypeFieldsStyleMapper {
    void addBatch(List<LayerTypeStyle> list);


    void deleteByLayerTypeId(String layerTypeId);

    List<LayerTypeStyle> findByLayerTypeId(LayerTypeStyleVo layerTypeStyle);

    /**
     * 查询相关图层样式
     */
    List<LayerTypeStyle> selectLayerTypeStyles(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("abbreviation") String abbreviation);

    void deleteBatch(List<LayerTypeStyle> list);

    void updateBatch(List<LayerTypeStyle> list);
}
