package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.CamaraDeviceParamsInfoVO;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.entity.GisSdkReq;
import com.chinatower.gis.api.entity.gis.*;
import com.chinatower.gis.api.entity.layer.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @desription: gis pg sdk
 * @data 创建时间：2024/1/10 9:44
 * @author chuan hao
 * @version 1.0
 *
 */
public interface GisPgMapper {

    @DS("postgres")
    List<Integer> altitudeHeightQuery(@Param("lon") String lon, @Param("lat") String lat);

    /**
     * 批量查询海拔高度
     */
    @DS("postgres")
    List<AltitudesRes> altitudeHeightQuerys(@Param("LonLat") String LonLat);

    /**
     * 批量查询海拔高度
     */
    @DS("postgres")
    CameraCoordRes getCameraCoord(CamaraDeviceParamsInfoVO camaraDeviceParamsInfoVO);

    /**
     * 查询对应表名是存在
     */
    @DS("postgres")
    int selectTableNameCount(@Param("tableNames")List<String> tableNames);
    /**
     * 耕地类型
     */
    @DS("postgres")
    List<LayerXztbDlbmAreaRes> layerXztbDlbmAreaRes(@Param("tableName")String tableName,@Param("areaCode")String areaCode);
    /**
     * 耕地等别
     */
    @DS("postgres")
    List<LayerXztbGddbAreaChildRes> layerXztbGddbAreaRes(@Param("tableName")String tableName,@Param("areaCode")String areaCode);

    /**
     * 年份变化
     */
    @DS("postgres")
    List<LayerAnnualChangesRes> layerAnnualChangesRes(@Param("tableNames")List<LayerYearTableName> tableNames, @Param("areaCode")String areaCode,@Param("type")Integer type);
    
    /**
     * 查询坡度坡向
     */
    @DS("postgres")
    GisSlopeAspectRes gisSlopeAspectRes(@Param("lon")String lon,@Param("lat")String lat);

    /**
     * 查询林种
     */
    @DS("postgres")
    Integer selectGlobalLinZhong(@Param("lon")String lon,@Param("lat")String lat);

    /**
     * 林草
     * @param lon
     * @param lat
     * @return
     */
    @DS("postgres")
    GisFireCombustibleRes selectGlobalLinCao(@Param("lon")String lon, @Param("lat")String lat);

    /**
     * 查询植被覆盖
     */
    @DS("postgres")
    List<GisVegetationDataRes> gisVegetationDataRes(@Param("lon")String lon, @Param("lat")String lat,@Param("distance")Integer distance);

    /**
     *
     * @param lon
     * @param lat
     * @return 查询火线等级
     */
    @DS("postgres")
    Integer selectFireLevel(@Param("lon")String lon, @Param("lat")String lat);

    /**
     * 查询坡度
     */
    @DS("postgres")
    Double selectSlope(@Param("tableName")String tableName,@Param("lon")String lon,@Param("lat")String lat);

    /**
     * 查询坡向
     */
    @DS("postgres")
    Double selectAspect(@Param("tableName")String tableName,@Param("lon")String lon,@Param("lat")String lat);

    @DS("postgres")
    Map<String,Object> polygonClipRasterStats(@Param("wkt")String wkt,@Param("sampleNum")Integer sampleNum);

    @DS("postgres")
    List<profileAnalysisOneRes> profileAnalysisOne(@Param("wkt")String wkt, @Param("sampleNum")Integer sampleNum);
    @DS("postgres")
    List<profileAnalysisTwoRes> profileAnalysisTwo(@Param("wkt")String wkt, @Param("sampleNum")Integer sampleNum);

    @DS("postgres")
    Double measureDistanceLow(@Param("wkt")String wkt,@Param("sampleNum")Integer sampleNum);
    @DS("postgres")
    Double measureDistanceHigh(@Param("wkt")String wkt,@Param("sampleNum")Integer sampleNum);

    @DS("postgres")
    Double measureAreaLow(@Param("wkt")String wkt,@Param("sampleNum")Integer sampleNum);
    @DS("postgres")
    Double measureAreaHigh(@Param("wkt")String wkt,@Param("sampleNum")Integer sampleNum);
}
