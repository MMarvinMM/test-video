package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.bdm.modules.layer.domain.LayerTypeStyleMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LayerTypeStyleMappingMapper {

    /**
     * 新增
     **/
    int insert(LayerTypeStyleMapping layerTypeStyleMapping);

    /**
     * 批量添加
     */
    int saveBatch(@Param("list") List<LayerTypeStyleMapping> list);

    /**
     * 批量修改
     */
    int updateBatchById(@Param("list") List<LayerTypeStyleMapping> list);

    /**
     * 刪除
     **/
    int delete(@Param("typeStyleId")String typeStyleId);

    /**
     * 删除
     */
    int deletes(@Param("typeStyleIds")List<String> typeStyleIds);

    /**
     * 更新
     **/
    int update(LayerTypeStyleMapping layerTypeStyleMapping);

    /**
     *
     * @param styleId 样式ID
     */
    List<LayerTypeStyleMapping> selectByStyleId(@Param("styleId") String styleId,@Param("geoType")String geoType);

    /**
     * 根据图层类型查询
     */
    List<LayerTypeStyleMapping> selectByLayerTypeId(@Param("layerTypeIds") List<String> layerTypeIds,@Param("tenantId")String tenantId,@Param("industryCode")String industryCode);

    /**
     * 根据图层类型简称查询
     */
    List<LayerTypeStyleMapping> selectByAbbreviations(@Param("abbreviations") List<String> abbreviations);

    /**
     * 样式类型ID
     */
    List<LayerTypeStyleMapping> selectByTypeId(@Param("typeIds") List<String> typeIds,@Param("tenantId")String tenantId,@Param("industryCode")String industryCode);

    /**
     * 查询默认ID
     */
    String getLayerTypeStyleDefaultShowId(@Param("tenantId")String tenantId,@Param("industryCode")String industryCode,@Param("layerTypeId")String layerTypeId);
}
