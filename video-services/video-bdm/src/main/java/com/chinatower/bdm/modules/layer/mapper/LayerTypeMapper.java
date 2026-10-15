package com.chinatower.bdm.modules.layer.mapper;


import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.common.core.dto.bdm.LayerTypeListVO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 图层类别管理 Mapper 接口
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
public interface LayerTypeMapper {
    /**
     * 查询分类
     * @param id
     * @return
     */
    LayerType selectById(@Param("id")String id);

    /**
     * 查询分类
     * @param abbreviation 简称
     * @return
     */
    LayerType selectByLayerTypeSimple(@Param("abbreviation")String abbreviation);

    /**
     *
     * @param layerType
     * @return 新增
     */
    Integer insert(LayerType layerType);

    /**
     *
     * @param id
     * @return 单个删除
     */
    Integer deleteById(@Param("id")String id);
    /**
     *
     * @param layerType
     * @return 修改
     */
    Integer updateById(LayerType layerType);
    /**
     * @param layerType
     * @return 查询列表
     */
    List<LayerType> selectList(LayerType layerType);


    /**
     * @param list
     * @return 保存集合
     */
    Integer saveBatch(@Param("list") List<LayerType> list);

    /**
     * @param ids
     * @return 批量删除
     */
    Integer deleteBatchIds(@Param("ids") List<String> ids);

    /**
     *
     * @param list
     * @return 批量修改
     */
    Integer updateBatchById(@Param("list") List<LayerType> list);

    /**
     * @param ids
     * @return 用id查询
     */
    List<LayerType> selectByIds(@Param("ids") List<String> ids);

    /**
     * 查询图层类型名称是否存在
     */
    Integer selectCount(@Param("tenantId") String tenantId,
                      @Param("industryCode") String industryCode,
                      @Param("typeNames") List<String> typeNames);

    List<LayerType> getListByAbbreviation(@Param("tenantId") String tenantId,
                                                   @Param("industryCode") String industryCode,
                                                   @Param("list") List<LayerType> list);

    LayerType getListByLayerTypeName(@Param("tenantId") String tenantId,
                                          @Param("industryCode") String industryCode,
                                          @Param("item") LayerType item);
    /**
     * 根据类型查询简称
     */
    LayerType selectByAbbreviation(@Param("abbreviation") String abbreviation,
                                   @Param("tenantId") String tenantId,
                                   @Param("industryCode") String industryCode);
    /**
     * 查询图层类型字段
     */
    List<SysGisFieldsDTO> getLayerTypeFields(@Param("abbreviation")String abbreviation);

    /**
     * 图层类别列表
     */
    List<LayerTypeListVO> selectListByCondition(@Param("tenantId") String tenantId,
                                                @Param("industryCode") String industryCode,
                                                @Param("orderBy") String orderBy,
                                                @Param("dto") ListByConditionVO dto);

    void insertAbbreviation(@Param("abbs") List<String> abbs);

    List<String> selectAbbreviationSql();

    void createTypetableback(@Param("typetableback") String typetableback);

    void createTypefieldstableback(@Param("typefieldstableback") String typefieldstableback);
}
