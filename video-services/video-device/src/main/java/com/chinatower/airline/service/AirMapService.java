package com.chinatower.airline.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.request.AlgorithmReq;
import com.chinatower.airline.dto.request.CompareImageReq;
import com.chinatower.airline.dto.response.AlgorithmResultDTO;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import com.chinatower.airline.dto.response.AirMapAnnotationRes;
import com.chinatower.airline.dto.response.CorrespondPointRes;
import com.chinatower.airline.dto.response.FlyAreaRes;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.event.entity.vo.SecondRecogBatchVO;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 13:20
 */
public interface AirMapService {

    /**
     * @param annotationMapReq
     * @return
     */
    Result addAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     * @param annotationMapReq
     * @return
     */
    List<AirMapAnnotationRes> queryAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     * @param annotationMapReq
     * @return
     */
    Result delAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param annotationMapReq
     * @return
     */
    Result defaultAnnotationMapName(AnnotationMapReq annotationMapReq);

    /**
     * 
     * @param annotationMapReq
     * @return
     */
    Result checkAnnotationMapName(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    Result addFlyArea(FlyAreaReq flyAreaReq);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    Result delFlyArea(FlyAreaReq flyAreaReq);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    List<FlyAreaRes> queryFlyAreaList(FlyAreaReq flyAreaReq);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    Result checkFlyAreaName(FlyAreaReq flyAreaReq);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    Result editFlyArea(FlyAreaReq flyAreaReq);

    /**
     * 对照点 新增/编辑
     * @param correspondPointReq
     * @return
     */
    Result editCorrespondPoint(CorrespondPointReq correspondPointReq);

    /**
     * 对照点 删除
     * @param correspondPointReq
     * @return
     */
    Result delCorrespondPoint(CorrespondPointReq correspondPointReq);

    /**
     * 对照点 查询
     * @param correspondPointReq
     * @return
     */
    List<CorrespondPointRes> queryCorrespondPointList(CorrespondPointReq correspondPointReq);

    /**
     * 校验对照点名称唯一
     * @param correspondPointReq
     * @return
     */
    Result checkCorrespondPointName(CorrespondPointReq correspondPointReq);

    /**
     * 查询对应航线
     * @param flyAreaReq
     * @return
     */
    List<AirLineVo> queryAirLineByFlyArea(FlyAreaReq flyAreaReq);

    /**
     *
     * @param compareImageReq
     * @return
     */
    Result compareImageCollectEdit(CompareImageReq compareImageReq);

    /**
     *
     * @param algorithmReq
     * @return
     */
    JSONObject queryAlgorithmsResult(AlgorithmReq algorithmReq);
}
