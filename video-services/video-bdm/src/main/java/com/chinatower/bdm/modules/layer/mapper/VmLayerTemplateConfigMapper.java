package com.chinatower.bdm.modules.layer.mapper;

import com.chinatower.common.core.dto.bdm.LayerTempConfigVO;
import com.chinatower.common.core.dto.bdm.VmLayerTempConfigDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 图层模板配置mapper层
 * @author lyy
 * @version v1.6.60
 * @date 2024/04/29 14:43
 */
@Mapper
public interface VmLayerTemplateConfigMapper {
    /**
     * 根据条件查询图层模板配置信息
     * @param layerTempConfigDTO
     * @return List<VmLayerTempConfigVO>
     */
    List<LayerTempConfigVO> getLayerTempConfigList(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 根据图层模板ID查询图层属性配置
     * @return VmLayerTempConfigVO
     */
    LayerTempConfigVO queryLayerTemplateIsExists(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 根据id查询图层模板
     * @param id
     * @return
     */
    LayerTempConfigVO selectLayerTempById(@Param("id") String id);

    /**
     * 查询初始化图层模板
     * @param configDTO
     * @return
     */
    LayerTempConfigVO selectInitLayerTempByType(VmLayerTempConfigDTO configDTO);

    /**
     * 校验图层模板名称是否已存在
     * @param layerTempConfigDTO
     * @return int
     */
    int getLayerTempNameCount(VmLayerTempConfigDTO layerTempConfigDTO);

    /**
     * 新增图层模板配置
     * @param tempConfigVO
     * @return Integer
     */
    Integer addVmLayerTemplateConfig(LayerTempConfigVO tempConfigVO);

    /**
     * 修改图层模板配置
     * @param tempConfigVO
     * @return
     */
    Integer updateLayerTempConfigById(LayerTempConfigVO tempConfigVO);

    /**
     * 根据图层模板ID查询卫片执法数据配置
     * @param tenantId
     * @param industryCode
     * @param platformCode
     * @param layerTempId
     * @return
     */
    Integer selectSatelliteLayerCount(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                                      @Param("platformCode") String platformCode, @Param("layerTempId") String layerTempId);

    /**
     * 逻辑删除图层模板配置
     * @param id
     * @return int
     */
    int delLayerTempById(@Param("id") String id);

    /**
     * 根据简称查询图层模板
     * @param configVOS
     * @return
     */
    List<LayerTempConfigVO> selectLayerByAbbreviation(@Param("list") List<LayerTempConfigVO> configVOS);

    /**
     * 根据告警类型获取图层模板
     * @param configDTO
     * @return
     */
    List<Map<String, Object>> getLayerTempByTypeList(VmLayerTempConfigDTO configDTO);

}
