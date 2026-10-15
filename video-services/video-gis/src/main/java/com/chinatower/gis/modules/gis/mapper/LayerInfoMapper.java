package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.layer.GeometrysRes;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisSerialItem;
import com.chinatower.gis.modules.gis.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 图层信息
 * @data 创建时间：2023/8/10 21:06
 */
@Mapper
public interface LayerInfoMapper {
    /**
     * @param layerId 图层ID
     * @return 图层详情
     */
    @DS("postgres")
    LayerInfo selectById(@Param("layerId") String layerId);

    @DS("postgres")
    List<LayerInfo> selectByIds(@Param("ids") List<String> ids);

    /**
     * @param layer 新增图层信息
     * @return 新增
     */
    @DS("postgres")
    Integer insert(LayerInfo layer);

    /**
     * @param ids
     * @return 删除图层
     */
    @DS("postgres")
    Integer deleteBatchIds(@Param("ids") List<String> ids);

    /**
     * @param tableName
     * @return 删除表
     */
    @DS("postgres")
    int deleteTable(@Param("tableName") String tableName);
    @DS("postgres")
    int deleteTablesView(@Param("tableNames") List<String> tableNames);
    @DS("postgres")
    int deleteTables(@Param("tableNames") List<String> tableNames);
    @DS("postgres")
    int deleteTablesSeq(@Param("tableNames") List<String> tableNames);

    /**
     * @param tenantId 租户ID
     * @param industryCode 行业类型
     * @return 根据tenant_id、industry_code 查询所有图层名称
     */
    @DS("postgres")
    List<String> selectByTenant(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("layerType") String layerType);

    @DS("postgres")
    Integer getCount(@Param("tableName")String tableName);

    /**
     * 查询图层表名
     */
    @DS("postgres")
    List<LayerInfo> layerTables(@Param("layerIds") List<String> layerIds);

    /**
     *
     * @param tables
     * @return
     */
    @DS("postgres")
    List<String> getGeometrys(@Param("tables") List<LayerInfo> tables);
}
