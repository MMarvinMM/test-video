package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.gis.api.entity.layer.GetXjxlFeaturesByIdsReq;
import com.chinatower.gis.api.entity.layer.GetXlsjFeaturesReq;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.entity.gis.SysGisFieldsDictDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

@Mapper
public interface PgLayerCommonMapper {


    /**
     * 查询图层类型字段
     */
    List<SysGisFieldsDTO> getLayerTypeFields(@Param("abbreviation") String abbreviation);

    /**
     * 图层类型字典值
     */
    List<SysGisFieldsDictDTO> getLayerTypeDictFields(@Param("abbreviation") String abbreviation);

    /**
     * 查询图斑编号字段
     */
    String getLayerTbbhField(@Param("abbreviation") String abbreviation);

    /**
     * 面积字段
     */
    String getLayerAreaField(@Param("abbreviation") String abbreviation);

    /**
     * 获取面积
     */
    double getLayerAreaFromSysLayer(@Param("layerID") String layerID);

    /**
     * 获得默认图层名称
     */
    String getAbbreviationLayerName(@Param("tenantId") String tenantId,
                                    @Param("industryCode") String industryCode,
                                    @Param("abbreviation") String abbreviation);

    /**
     * @return 创建表
     */
    @DS("postgres")
    int createTable(@Param("sql") String sql);

    /**
     * 备份表
     */
    @DS("postgres")
    int editLayer(@Param("layerName") String layerName,@Param("bakLayerName")String bakLayerName);

    @DS("postgres")
    int editBackLayer(@Param("layerName") String layerName,@Param("bakLayerName")String bakLayerName);

    /**
     * @param tableName 表名
     * @param fields    其余字段
     * @return 创建视图
     */
    @DS("postgres")
    int createTableView(@Param("tableName") String tableName, @Param("fields") String fields);

    /**
     * 新增
     *
     * @author ch
     * @date 2023/08/11
     **/
    @DS("postgres")
    int saveBatch(@Param("sql") String sql);

    /**
     * @param tableName 表名
     * @return 获得图层总面积
     */
    @DS("postgres")
    Double getLayerArea(@Param("tableName") String tableName, @Param("field") String field);

    /**
     * @param tableName
     * @return 计算面积
     */
    @DS("postgres")
    int updateCalarea(@Param("tableName") String tableName);

    /**
     * 探而未采
     */
    @DS("postgres")
    int updateTewcCalarea(@Param("tewc") String tewc,@Param("tkq") String tkq,@Param("areaCode") String areaCode);

    /**
     * @param tableName 表名
     * @return 删除错误数据
     */
    @DS("postgres")
    Integer deleteBadData(@Param("tableName") String tableName, @Param("mustFieldSql") String mustFieldSql);

    /**
     * @param req
     * @return 巡检路线查询
     */
    @DS("postgres")
    String inspectionRoute(@Param("req") GetXjxlFeaturesByIdsReq req,
                           @Param("tableName") String tableName,
                           @Param("baseFields") String baseFields,
                           @Param("allFields") String allFields,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size);

    /**
     * @param req       初心参数
     * @param tableName 表名
     * @return 巡检线路数量
     */
    @DS("postgres")
    Integer inspectionRouteCount(@Param("req") GetXjxlFeaturesByIdsReq req,
                                 @Param("tableName") String tableName);

    /**
     * @param req       请求参数
     * @param tableName 表名
     * @return 查询巡检下拉数据
     */
    @DS("postgres")
    String getXlsjFeatures(@Param("req") GetXlsjFeaturesReq req,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size,
                           @Param("tableName") String tableName,
                           @Param("tbbh") String tbbh,
                           @Param("baseFields") String baseFields);

    /**
     * @param req       请求参数
     * @param tableName 表名
     * @return 查询巡检下拉数据总数量
     */
    @DS("postgres")
    Integer getXlsjFeaturesCount(@Param("req") GetXlsjFeaturesReq req,
                                 @Param("tableName") String tableName);

    @DS("postgres")
    int updateSRID(@Param("tableName") String tableName);

    /**
     * @return 创建表
     */
    @DS("postgres")
    int createTableName(@Param("exploreLayerName") String exploreLayerName, @Param("tableName") String tableName);


    /**
     * 计算探而未采
     *
     * @param exploreLayerName    探图层
     * @param collectingLayerName 采图层
     */
    @DS("postgres")
    int calculateLayer(@Param("exploreLayerName") String exploreLayerName,
                       @Param("collectingLayerName") String collectingLayerName,
                       @Param("tableName") String tableName,
                       @Param("fields") String fields);

    /**
     * 修改过期状态
     */
    @DS("postgres")
    int updateExpire(@Param("tableName") String tableName);

    /**
     * 查询未删除的探矿权、采矿权、探而未采图层名称
     */
    List<String> updateExpireTableName();

    @DS("postgres")
    List<Map<String,Object>> tableNameList(@Param("bsm") String bsm,
                                           @Param("tableName") String tableName,
                                           @Param("fields") String fields,
                                           @Param("offset") Integer offset,
                                           @Param("size") Integer size);

    @DS("postgres")
    List<LayerSlzyStatisticsDTO> layerClassifyAreaStatistics(@Param("vo")LayerClassifyAreaStatisticsVO vo);

}
