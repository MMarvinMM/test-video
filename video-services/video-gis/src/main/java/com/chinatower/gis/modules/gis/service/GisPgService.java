package com.chinatower.gis.modules.gis.service;


import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.CamaraDeviceParamsInfoVO;
import com.chinatower.gis.api.entity.AltitudesReq;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.gis.api.entity.TableNameListReq;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.entity.gis.GisFireCombustibleRes;
import com.chinatower.gis.api.entity.gis.GisFireGradeRes;
import com.chinatower.gis.api.entity.gis.GisSlopeAspectRes;
import com.chinatower.gis.api.entity.gis.GisVegetationDataRes;
import com.chinatower.gis.api.entity.layer.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface GisPgService {

    /**
     * 查询海拔高德
     * @param lon 经度
     * @param lat 维度
     */
    Integer altitudeHeightQuery(String lon, String lat);

    /**
     * 批量查询海拔高德
     */
    Result<List<AltitudesRes>> altitudeHeightQuerys(AltitudesReq req);

    /**
     * 查询图层数据
     */
    List<Map<String,Object>> tableNameList(TableNameListReq req);

    /**
     * 获取摄像头信息
     *
     * @param camaraDeviceParamsInfoVO 设备编码
     */
    CameraCoordRes getCameraCoord(CamaraDeviceParamsInfoVO camaraDeviceParamsInfoVO);

    /**
     * 所有表分类统计的面积信息
     */
    List<LayerSlzyStatisticsDTO> layerClassifyAreaStatistics(LayerClassifyAreaStatisticsVO vo);

    /**
     * 耕地类型
     */
    List<LayerXztbDlbmAreaRes> layerXztbDlbmAreaRes(LayerYearTableName vo);

    /**
     * 等地等别
     */
    LayerXztbGddbAreaRes layerXztbGddbAreaRes(LayerYearTableName vo);

    /**
     * 年度变化
     */
    List<LayerAnnualChangesRes> layerAnnualChangesRes(LayerAnnualChangesReq vo);

    /**
     * 坡度坡向
     */
    GisSlopeAspectRes GisSlopeAspectRes(String lon, String lat) throws ExecutionException, InterruptedException;

    /**
     * 火势蔓延
     */
    GisFireCombustibleRes gisFireCombustibleRes(String lon, String lat) throws ExecutionException, InterruptedException;

    /**
     * 火灾等级
     */
    GisFireGradeRes gisFireGradeRes(String lon, String lat) throws ExecutionException, InterruptedException;

    /**
     * 植被覆盖
     */
    List<GisVegetationDataRes> gisVegetationDataRes(String lon, String lat,Integer distance);
}
