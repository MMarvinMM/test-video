package com.chinatower.video.modules.query.dao;

import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.video.api.entity.VideoPtzQueryReq;
import com.chinatower.video.modules.query.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: VideoPtzQueryDAO
 * @Description: 视频Ptz查询DAO
 * @Author: zhujun
 * @Date: 2022-05-19
 **/
@Mapper
public interface VideoPtzQueryDAO {

    DeviceModelVO getDeviceModel(VideoPtzQueryReq videoPtzQueryReq);

    List<DeviceModelVO> getDeviceList(FocusThisPointReq focusThisPointReq);

    /**
     *  通过设备编码获取有效参考点
     */
    List<DeviceRefPointVO> getDevicePointsValid(@Param("deviceCode") String deviceCode);

    List<DeviceRefPointVO> getDevicePointsValidByReal(@Param("deviceCode") String deviceCode);
    /**
     *  通过设备编码获取参考点平均偏差角度
     */
    DeviceRefPointVO getDeviceAvgPointsValid(@Param("deviceCode") String deviceCode);

    List<DeviceRefPointVO> getDevicePointsValidBatch(List<String> list);

    List<DeviceModelDTO> getDeviceModelForArMove(String deviceCode);

    List<ArTagMoveRes> getArTagByVideo(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByMap(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByDevice(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByIot(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByUav(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagBySensor(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByHorn(ArTagFilterVO arTagFilterVO);

    List<ArTagMoveRes> getArTagByLayer(ArTagFilterVO arTagFilterVO);

    List<PolygonPatchConfig> queryPolygonByLayerId(ArLayerTagReq ArLayerTagReq);
}
