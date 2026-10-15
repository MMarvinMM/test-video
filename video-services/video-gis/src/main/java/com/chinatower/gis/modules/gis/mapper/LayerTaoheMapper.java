package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.GeoExtent;
import com.chinatower.gis.modules.gis.entity.layer.*;
import com.chinatower.platform.annotation.DataPermissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription 套合分析相关数据库查询
 * @data 创建时间：2023/8/10 21:06
 */
@Mapper
public interface LayerTaoheMapper {

    /**
     * @param guid    guid
     * @param layerId 图层ID
     * @return 图层详情
     */
    @DS("postgres")
    TaoheAnalysisStruct getTaoheAnalysisStruct(@Param("guid") String guid, @Param("layerId") String layerId);

    /**
     * @param count          数量
     * @param offset         偏移
     * @param tmpName        临时表名
     * @param sourcTableName 源表名
     * @return 获取标识码列表
     */
    @DS("postgres")
    ArrayList<String> selectTaoheAnalysisLayerTableBsm(@Param("count") int count, @Param("offset") int offset, @Param("tmpName") String tmpName, @Param("sourcTableName") String sourcTableName);

    /**
     * @param cols    列字符串
     * @param sqlRest 联查的表
     * @return 获取表的geojson格式的对象
     */
    @DS("postgres")
    String selectTaoheAnalysisLayerTableObj(@Param("cols") String cols, @Param("sqlRest") String sqlRest);

    /**
     * @param guid guid
     * @return 查询该临时表的总数量
     */
    @DS("postgres")
    String selectTaoheAnalysisLayerTableAllCountStr(@Param("guid") String guid);

    /**
     * @param tmpName        临时表名
     * @param sourcTableName 原始表名
     * @return 查询该临时表的总数量
     */
    @DS("postgres")
    ArrayList<TaoheAnalysisSerialItem> selectTaoheAnalysisSerialItems(@Param("tmpName") String tmpName, @Param("sourcTableName") String sourcTableName);

    @DataPermissions(layerColumn = "lyr.id")
    ArrayList<TaoheLayerInfo> selectTaoheAnalysisLayers(@Param("dto") LayerQueryDTO dto, @Param("typeSimplesString") String typeSimplesString);

    @DataPermissions(layerColumn = "lyr.id")
    ArrayList<TaoheLayerInfo> selectTaoheAnalysisLayersExt(@Param("dto") LayerQueryDTO dto, @Param("typeSimplesString") String typeSimplesString, @Param("slqExt") String slqExt);

    @DataPermissions(layerColumn = "lyr.id")
    ArrayList<TaoheLayerInfo> selectIntersectAnalysisLayersExt(@Param("dto") LayerQueryDTO dto, @Param("slqExt") String slqExt);

    @DS("postgres")
    ArrayList<XztbCal> selectXZTBTmpValue(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("serialColName") String serialColName, @Param("overArea") double overArea, @Param("offset") int offset, @Param("limit") int limit);

    @DS("postgres")
    ArrayList<XztbCal> selectXZTBTmpValue3(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("serialColName") String serialColName, @Param("overArea") double overArea);

    @DS("postgres")
    ArrayList<XztbCal> selectXZTBTmpValue4(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("serialColName") String serialColName, @Param("overArea") double overArea);

    @DS("postgres")
    ArrayList<YjjbntCal> selectYJJBNTTmpValue(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("overArea") double overArea, @Param("offset") int offset, @Param("limit") int limit);

    @DS("postgres")
    ArrayList<YjjbntCal> selectYJJBNTTmpValue3(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("overArea") double overArea);

    @DS("postgres")
    ArrayList<YjjbntCal> selectYJJBNTTmpValue4(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("areaColName") String areaColName, @Param("overArea") double overArea);

    @DS("postgres")
    int selectTaoheAnalysisCountByLayerName(@Param("tbName") String tbName);

    @DS("postgres")
    int selectCalIntersectsCount(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance, @Param("maxIntersectsCount") int maxIntersectsCount);

    @DS("postgres")
    String selectTableColsByTableName(@Param("sourcTableName") String sourcTableName);

    LayerTypeAndName selectLayerTypeAndNameBylayerID2(@Param("lyrid") String lyrid);

    @DS("postgres")
    YjjbntCal selectAreaByBsm(@Param("tableName") String tableName, @Param("areaColName") String areaColName, @Param("bsm") String bsm);

    @DS("postgres")
    double selectCalculateAea(@Param("wkt") String wkt, @Param("distance") double distance, @Param("inProject") int inProject);

    String getLayerName(@Param("layerId") String layerId);

    @DS("postgres")
    boolean selectTestExistTable(@Param("tableName") String tableName);

    @DS("postgres")
    ArrayList<String> selectListString(@Param("sql") String sql);

    @DS("postgres")
    void createInOutBalanceSourceTable(@Param("tableName") String tableName);

    @DS("postgres")
    void deleteInOutBalanceTable(@Param("tableName") String tableName);

    @DS("postgres")
    void insertInOutBalanceTable(@Param("insertTableName") String insertTableName, @Param("selectTableName") String selectTableName, @Param("where") String where);

    @DS("postgres")
    void createInOutBalanceResultTable(@Param("tableName") String tableName);

    @DS("postgres")
    void insertInOutBalanceResultTable(@Param("targetTableName") String targetTableName, @Param("tableName1") String tableName1, @Param("tableName2") String tableName2, @Param("areaTolerance") double areaTolerance);

    @DS("postgres")
    void createjcphitem(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("userId") String userId, @Param("appVerCode") String appVerCode, @Param("guid") String guid);

    @DS("postgres")
    int deleteJcphitemByGuid(@Param("guid") String guid);

    @DS("postgres")
    void updateJcphmanageInfo(@Param("preLayerName") String preLayerName, @Param("curLayerName") String curLayerName, @Param("g1") String g1, @Param("f1") String f1, @Param("g2") String g2, @Param("f2") String f2, @Param("gdlc") String gdlc, @Param("gdlr") String gdlr, @Param("lc0101") double lc0101, @Param("lc0102") double lc0102, @Param("lc0103") double lc0103, @Param("lr0101") double lr0101, @Param("lr0102") double lr0102, @Param("lr0103") double lr0103, @Param("guid") String guid);

    @DS("postgres")
    void updateJcphmanageDeleteTable(@Param("guid") String guid);

    @DS("postgres")
    List<GetXlsjFeatureRes> selectJcphResult(@Param("tableName") String tableName, @Param("dlbmColName") String dlbmColName, @Param("where") String where);

    @DS("postgres")
    GetInOutBalanceRes selectGetInOutBalanceRes(@Param("guid") String guid);

    @DataPermissions(layerColumn = "lyr.id")
    String selectAbbreviationByTableName(@Param("dto") LayerQueryDTO dto, @Param("tableName") String tableName);

    @DS("postgres")
    String selectGuidByTableNames(@Param("preLayerName") String preLayerName, @Param("curLayerName") String curLayerName);

    @DS("postgres")
    GetInOutBalanceExtendRes selectUpdateTimeByGuid(@Param("guid") String guid);

    @DS("postgres")
    int selectInOutBalanceExistByGuid(@Param("guid") String guid);

    @DS("postgres")
    String selectTableNameByGuid(@Param("guid") String guid);

    @DS("postgres")
    boolean selectIsDeleteByGuid(@Param("guid") String guid);

    @DS("postgres")
    List<String> getLayerNamesByGuid(@Param("newTableName") String newTableName);

    @DS("postgres")
    IntersectAnalysisItem selectIntersectAnalysis(@Param("layerName") String layerName, @Param("areaField") String areaField, @Param("geoSql") String geoSql);

    @DS("postgres")
    int selectLengthByTableBsm(@Param("layerName") String layerName);

    @DS("postgres")
    int autoCompareLayerFeatures(@Param("preLayerName") String preLayerName, @Param("curLayerName") String curLayerName, @Param("tolerance") double tolerance);

    @DS("postgres")
    int countTableRows(@Param("layerName") String layerName);

    String getTableNameByLayerId(@Param("layerId") String layerId);

    @DataPermissions(layerColumn = "lyr.id")
    ArrayList<TaoheLayerInfo> selectIntersectingLayers2(@Param("dto") LayerQueryDTO dto);

    @DataPermissions(layerColumn = "lyr.id")
    List<TaoheLayerInfo> selectLayerTbbhs(@Param("dto") LayerQueryDTO dto, @Param("layerIds") String layerIds);

    @DS("postgres")
    int isIntersectingPlot(@Param("tbName") String tbName, @Param("wkt") String wkt, @Param("distance") double distance);

    @DS("postgres")
    GeoExtent selectGeoExtent(@Param("wkt") String wkt, @Param("distance") double distance);

    @DataPermissions(layerColumn = "lyr.id")
    ArrayList<TaoheLayerInfo> selectIntersectingLayers(@Param("dto") LayerQueryDTO dto, @Param("geoExtent") GeoExtent geoExtent);

}