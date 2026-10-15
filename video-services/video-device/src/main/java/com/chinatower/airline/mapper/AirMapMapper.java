package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.request.CompareImageReq;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import com.chinatower.airline.dto.response.CorrespondPointRes;
import com.chinatower.airline.dto.response.DeviceAirLineRes;
import com.chinatower.airline.dto.response.FlyAreaRes;
import com.chinatower.airline.dto.response.AirMapAnnotationRes;
import com.chinatower.device.entity.vo.AirLineVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 13:23
 */
@Mapper
public interface AirMapMapper {

    /**
     * @param annotationMapReq
     */
    Integer addAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     * @param annotationMapReq
     * @return
     */
    List<AirMapAnnotationRes> queryAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param annotationMapReq
     */
    void delAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param annotationMapReq
     */
    void updateAnnotationMap(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param annotationMapReq
     * @return
     */
    int defaultAnnotationMapName(AnnotationMapReq annotationMapReq);

    /**
     *
     * @param annotationMapReq
     * @return
     */
    AirMapAnnotationRes checkAnnotationMapName(AnnotationMapReq annotationMapReq);

    /**
     * 
     * @param id
     * @return
     */
    AnnotationMapReq queryAnnotationMapById(String id);

    /**
     *
     * @param flyAreaReq
     * @return
     */
    int addFlyArea(FlyAreaReq flyAreaReq);

    /**
     *
     * @param flyAreaReq
     */
    void delFlyArea(FlyAreaReq flyAreaReq);

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
    FlyAreaRes checkFlyAreaName(FlyAreaReq flyAreaReq);

    /**
     *
     * @param id
     * @return
     */
    FlyAreaRes queryFlyMapById(String id);

    /**
     *
     * @param flyAreaReq
     */
    void updateFlyArea(FlyAreaReq flyAreaReq);

    /**
     * 对照点 新增
     * @param correspondPointReq
     */
    void addCorrespondPoint(CorrespondPointReq correspondPointReq);

    /**
     * 对照点 编辑
     * @param correspondPointReq
     */
    void updateCorrespondPoint(CorrespondPointReq correspondPointReq);

    /**
     * 对照点 删除
     * @param correspondPointReq
     */
    void delCorrespondPoint(CorrespondPointReq correspondPointReq);

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
    int countByPointName(CorrespondPointReq correspondPointReq);

    /**
     * 查询航线列表
     * @param flyAreaReq
     * @return
     */
    List<AirLineVo> queryAirLineList(FlyAreaReq flyAreaReq);

    /**
     * 根据飞行区域查询航线
     * @param flyAreaRes
     * @return
     */
    List<AirLineVo> queryAirLineByFlyArea(FlyAreaReq flyAreaRes);

    /**
     * 查询设备
     * @param flyAreaReq
     * @return
     */
    List<DeviceAirLineRes> queryDevice(FlyAreaReq flyAreaReq);

    /**
     * 插入对比图片
     * @param compareImageReq
     */
    void insertCompareImageCollect(CompareImageReq compareImageReq);

    /**
     * 删除对比图片
     * @param compareImageReq
     */
    void deleteCompareImageCollect(CompareImageReq compareImageReq);
}
