package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.AisDetailsInfo;
import com.chinatower.outdata.api.entity.DetailsInfoResultVO;
import com.chinatower.outdata.api.entity.FixedTargetDTO;
import com.chinatower.outdata.api.entity.FixedTargetRecord;
import com.chinatower.outdata.api.entity.FixedTargetResultVO;
import com.chinatower.outdata.api.entity.GlobalTrackDTO;
import com.chinatower.outdata.api.entity.GlobalTrackResultVO;
import com.chinatower.outdata.api.entity.GlobalTrackTrackMap;
import com.chinatower.outdata.api.entity.RadarDeviceStatusVO;
import com.chinatower.outdata.api.entity.RadarReviewInfo;
import com.chinatower.outdata.api.entity.RadarShipPortraitAisReq;
import com.chinatower.outdata.api.entity.RadarShipPortraitAisRsp;
import com.chinatower.outdata.api.entity.RadarShipPortraitRadarReq;
import com.chinatower.outdata.api.entity.RadarShipPortraitRadarRsp;
import com.chinatower.outdata.api.entity.RadarTrackDTO;
import com.chinatower.outdata.api.entity.RadarTrackInfo;
import com.chinatower.outdata.api.entity.RelayDeviceInfo;
import com.chinatower.outdata.api.entity.RelayDeviceResultVO;
import com.chinatower.outdata.api.entity.ReviewInfoResultVO;
import com.chinatower.outdata.api.entity.SearchInfoResultVO;
import com.chinatower.outdata.api.entity.ShipPortraitAisResultVO;
import com.chinatower.outdata.api.entity.ShipPortraitRadarResultVO;
import com.chinatower.outdata.api.entity.ThermalMapInfo;
import com.chinatower.outdata.api.entity.ThermalMapInfoResultVO;
import com.chinatower.outdata.api.entity.TrackAllInfo;
import com.chinatower.outdata.api.entity.TrackInfoResultVO;
import com.chinatower.outdata.api.entity.TrackPointChartResultVO;
import com.chinatower.outdata.api.entity.TrackPointInfoResultVO;
import com.chinatower.outdata.api.entity.TrackPointListResultVO;
import com.chinatower.outdata.api.entity.UpdateFixedTargetIdList;
import com.chinatower.outdata.api.entity.UpdateFixedTargetResultVO;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 传感器状态上报
 * Created by zhujun on 2022/3/30.
 */
@Slf4j
@Component
public class RadarTargetFollowService {

    @Autowired
    private NetConnectUtils connectUtils;

    /**
     * 获取正在接力的摄像机
     * @param dto
     * @return
     */
    public Result<RelayDeviceInfo> queryRelayDeviceList(RadarDeviceStatusVO dto) {

        try {
            NetConnectUtils.RequestParamter<RadarDeviceStatusVO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取正在接力的相机接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_RELAY_DEVICE);
            //接口路径参数
            path.setDeviceCode(dto.getMessageId());
            //设置地址
            parameter.setPath(path);
            RelayDeviceResultVO resultVO = connectUtils.post(parameter, RelayDeviceResultVO.class);
            log.info("获取正在接力的相机接口返回参数{}", JSON.toJSONString(resultVO));
            if (null != resultVO && com.chinatower.platform.constants.Constants.ZERO.equals(resultVO.getCode())) {
                RelayDeviceInfo deviceCodeList = resultVO.getData();
                return Result.data(deviceCodeList);
            }
            return Result.fail("获取正在接力的相机接口请求失败:" + (resultVO != null ? resultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("获取正在接力的摄像机接口调用异常:", e);
            return Result.fail("获取正在接力的摄像机接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询目标回放数据
     * @param dto
     * @return
     */
    public Result<List<RadarReviewInfo>> queryRadarReviewInfoList(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("目标回放数据查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_RADAR_REVIEW_INFO);
            //设置地址
            parameter.setPath(path);
            ReviewInfoResultVO resultVO = connectUtils.post(parameter, ReviewInfoResultVO.class);
            log.info("目标回放数据查询接口返回参数{}", JSON.toJSONString(resultVO));
            if (null != resultVO && com.chinatower.platform.constants.Constants.ZERO.equals(resultVO.getCode())) {
                List<RadarReviewInfo> radarReviewInfoList = resultVO.getData();
                return Result.data(radarReviewInfoList);
            }
            return Result.fail("目标回放数据查询接口请求失败:" + (resultVO != null ? resultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("目标回放数据查询接口调用异常:", e);
            return Result.fail("目标回放数据查询接口调用异常:" + e.getMessage());
        }

    }

    /**
     * 查询目标轨迹
     * @param dto
     * @return
     */
    public Result<List<RadarTrackInfo>> queryRadarTrackInfoList(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("目标回放数据查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_RADAR_TRACK_INFO);
            //设置地址
            parameter.setPath(path);
            TrackInfoResultVO trackInfoResultVO = connectUtils.post(parameter, TrackInfoResultVO.class);
            log.info("目标轨迹查询接口返回参数{}", JSON.toJSONString(trackInfoResultVO));
            if (trackInfoResultVO != null && Constants.ZERO.equals(trackInfoResultVO.getCode())) {
                List<RadarTrackInfo> trackInfoList = trackInfoResultVO.getData();
                return Result.data(trackInfoList);
            }
            return Result.fail("目标轨迹查询接口请求失败:" + (trackInfoResultVO != null ? trackInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("目标轨迹查询接口调用异常:", e);
            return Result.fail("目标轨迹查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * AIS目标搜索
     * @param dto
     * @return
     */
    public Result<List<RadarReviewInfo>> queryAisSearch(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("AIS目标搜索接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_AIS_SEARCH);
            //设置地址
            parameter.setPath(path);
            SearchInfoResultVO searchInfoResultVO = connectUtils.post(parameter, SearchInfoResultVO.class);
            log.info("AIS目标搜索接口返回参数{}", JSON.toJSONString(searchInfoResultVO));
            if (searchInfoResultVO != null && Constants.ZERO.equals(searchInfoResultVO.getCode())) {
                List<RadarReviewInfo> infoList = searchInfoResultVO.getData();
                return Result.data(infoList);
            }
            return Result.fail("AIS目标搜索接口请求失败:" + (searchInfoResultVO != null ? searchInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("AIS目标搜索接口调用异常:", e);
            return Result.fail("AIS目标搜索接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询目标信息
     * @param dto
     * @return
     */
    public Result<AisDetailsInfo> queryAisDetails(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("目标信息查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_AIS_DETAILS);
            //设置地址
            parameter.setPath(path);
            DetailsInfoResultVO detailsInfoResultVO = connectUtils.post(parameter, DetailsInfoResultVO.class);
            log.info("目标信息查询接口返回参数{}", JSON.toJSONString(detailsInfoResultVO));
            if (detailsInfoResultVO != null && Constants.ZERO.equals(detailsInfoResultVO.getCode())) {
                AisDetailsInfo aisDetailsInfo = detailsInfoResultVO.getData();
                return Result.data(aisDetailsInfo);
            }
            return Result.fail("目标信息查询接口请求失败:" + (detailsInfoResultVO != null ? detailsInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("目标信息查询接口调用异常:", e);
            return Result.fail("目标信息查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询轨迹
     * @param dto
     * @return
     */
    public Result<List<TrackAllInfo>> queryTrajectoryTrack(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("轨迹查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TRAJECTORY_TRACK);
            //设置地址
            parameter.setPath(path);
            TrackPointListResultVO trackPointListResultVO = connectUtils.post(parameter, TrackPointListResultVO.class);
            log.info("轨迹查询接口返回参数{}", JSON.toJSONString(trackPointListResultVO));
            if (trackPointListResultVO != null && Constants.ZERO.equals(trackPointListResultVO.getCode())) {
                List<TrackAllInfo> trackAllInfoList = trackPointListResultVO.getData();
                return Result.data(trackAllInfoList);
            }
            return Result.fail("轨迹查询接口请求失败:" + (trackPointListResultVO != null ? trackPointListResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("轨迹查询接口调用异常:", e);
            return Result.fail("轨迹查询接口调用异常:" + e.getMessage());
        }
    }


    /**
     * 查询轨迹点信息
     * @param dto
     * @return
     */
    public Result<TrackAllInfo> queryTrajectoryTrackPoint(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("轨迹点信息查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TRAJECTORY_TRACKPOINT);
            //设置地址
            parameter.setPath(path);
            TrackPointInfoResultVO pointInfoResultVO = connectUtils.post(parameter, TrackPointInfoResultVO.class);
            log.info("轨迹点信息查询接口返回参数{}", JSON.toJSONString(pointInfoResultVO));
            if (pointInfoResultVO != null && Constants.ZERO.equals(pointInfoResultVO.getCode())) {
                TrackAllInfo trackAllInfo = pointInfoResultVO.getData();
                return Result.data(trackAllInfo);
            }
            return Result.fail("轨迹点信息查询接口请求失败:" + (pointInfoResultVO != null ? pointInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("轨迹点信息查询接口调用异常:", e);
            return Result.fail("轨迹点信息查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询轨迹分析
     * @param dtoList
     * @return
     */
    public Result<List<TrackAllInfo>> trackPointChartQuery(List<RadarTrackDTO> dtoList) {
        try {
            NetConnectUtils.RequestParamter<List<RadarTrackDTO>> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("轨迹分析查询接口入参{}", JSON.toJSONString(dtoList));
            //设置请求参数
            parameter.setBody(dtoList);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TRAJECTORY_TRACKPOINTCHARTQUERY);
            //设置地址
            parameter.setPath(path);
            TrackPointChartResultVO pointInfoResultVO = connectUtils.post(parameter, TrackPointChartResultVO.class);
            log.info("轨迹分析查询接口返回参数{}", JSON.toJSONString(pointInfoResultVO));
            if (pointInfoResultVO != null && Constants.ZERO.equals(pointInfoResultVO.getCode())) {
                List<TrackAllInfo> trackAllInfo = pointInfoResultVO.getData();
                return Result.data(trackAllInfo);
            }
            return Result.fail("轨迹分析查询接口请求失败:" + (pointInfoResultVO != null ? pointInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("轨迹分析查询接口调用异常:", e);
            return Result.fail("轨迹分析查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询AIS热力图
     * @param dto
     * @return
     */
    public Result<List<ThermalMapInfo>> queryTrajectoryThermalMap(RadarTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<RadarTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("AIS热力图查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TRAJECTORY_THERMALMAP);
            //设置地址
            parameter.setPath(path);
            ThermalMapInfoResultVO thermalMapInfoResultVO = connectUtils.post(parameter, ThermalMapInfoResultVO.class);
            log.info("AIS热力图查询接口返回参数{}", JSON.toJSONString(thermalMapInfoResultVO));
            if (thermalMapInfoResultVO != null && Constants.ZERO.equals(thermalMapInfoResultVO.getCode())) {
                List<ThermalMapInfo> thermalMapInfoList = thermalMapInfoResultVO.getData();
                return Result.data(thermalMapInfoList);
            }
            return Result.fail("AIS热力图查询接口请求失败:" + (thermalMapInfoResultVO != null ? thermalMapInfoResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("AIS热力图查询接口调用异常:", e);
            return Result.fail("AIS热力图查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 查询固定目标列表
     * @param dto
     * @return
     */
    public Result<FixedTargetRecord> queryFixedTarget(FixedTargetDTO dto) {
        try {
            NetConnectUtils.RequestParamter<FixedTargetDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("固定目标列表查询接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_FIXED_TARGET);
            //设置地址
            parameter.setPath(path);
            FixedTargetResultVO fixedTargetResultVO = connectUtils.post(parameter, FixedTargetResultVO.class);
            log.info("固定目标列表查询接口入参{}", JSON.toJSONString(fixedTargetResultVO));
            if (fixedTargetResultVO != null && Constants.ZERO.equals(fixedTargetResultVO.getCode())) {
                return Result.data(fixedTargetResultVO.getData());
            }
            return Result.fail("固定目标列表查询接口请求失败:" + (fixedTargetResultVO != null ? fixedTargetResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("固定目标列表查询接口调用异常:", e);
            return Result.fail("固定目标列表查询接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 修改固定目标状态
     * @param dto
     * @return
     */
    public Result<UpdateFixedTargetIdList> updateFixedTarget(FixedTargetDTO dto) {
        try {
            NetConnectUtils.RequestParamter<FixedTargetDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("修改固定目标状态接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_FIXED_TARGET);
            //设置地址
            parameter.setPath(path);
            UpdateFixedTargetResultVO updateFixedTargetResultVO = connectUtils.post(parameter, UpdateFixedTargetResultVO.class);
            log.info("修改固定目标状态接口返回参数{}", JSON.toJSONString(updateFixedTargetResultVO));
            if (updateFixedTargetResultVO != null && Constants.ZERO.equals(updateFixedTargetResultVO.getCode())) {
                UpdateFixedTargetIdList idList = updateFixedTargetResultVO.getData();
                return Result.data(idList);
            }
            return Result.fail("修改固定目标状态接口请求失败:" + (updateFixedTargetResultVO != null ? updateFixedTargetResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("修改固定目标状态接口调用异常:", e);
            return Result.fail("修改固定目标状态接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 获取全局轨迹
     * @param dto
     * @return
     */
    public Result<GlobalTrackTrackMap> globalTrack(GlobalTrackDTO dto) {
        try {
            NetConnectUtils.RequestParamter<GlobalTrackDTO> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取全局轨迹接口入参{}", JSON.toJSONString(dto));
            //设置请求参数
            parameter.setBody(dto);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TRAJECTORY_GLOBAL_TRACK);
            //设置地址
            parameter.setPath(path);
            GlobalTrackResultVO globalTrackResultVO = connectUtils.post(parameter, GlobalTrackResultVO.class);
            log.info("获取全局轨迹接口返回参数{}", JSON.toJSONString(globalTrackResultVO));
            if (globalTrackResultVO != null && Constants.ZERO.equals(globalTrackResultVO.getCode())) {
                GlobalTrackTrackMap globalTrackTrackMap = globalTrackResultVO.getData();
                return Result.data(globalTrackTrackMap);
            }
            return Result.fail("获取全局轨迹接口请求失败:" + (globalTrackResultVO != null ? globalTrackResultVO.getMsg() : null));
        }
        catch (Exception e) {
            log.info("获取全局轨迹接口调用异常:", e);
            return Result.fail("获取全局轨迹接口调用异常:" + e.getMessage());
        }
    }

    /**
     * AIS船舶画像
     * @param req
     * @return
     */
    public Result<RadarShipPortraitAisRsp> shipPortraitAis(RadarShipPortraitAisReq req) {
        try {
            NetConnectUtils.RequestParamter<RadarShipPortraitAisReq> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("AIS船舶画像接口入参{}", JSON.toJSONString(req));
            //设置请求参数
            parameter.setBody(req);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SHIPPORTRAIT_AIS);
            //设置地址
            parameter.setPath(path);
            ShipPortraitAisResultVO rsp = connectUtils.post(parameter, ShipPortraitAisResultVO.class);
            log.info("AIS船舶画像接口返回参数{}", JSON.toJSONString(rsp));
            if (rsp != null && Constants.ZERO.equals(rsp.getCode())) {
                RadarShipPortraitAisRsp data = rsp.getData();
                return Result.data(data);
            }
            return Result.fail("AIS船舶画像接口请求失败:" + (rsp != null ? rsp.getMsg() : null));
        }
        catch (Exception e) {
            log.info("AIS船舶画像接口调用异常:", e);
            return Result.fail("AIS船舶画像接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 雷达船舶画像
     * @param req
     * @return
     */
    public Result<RadarShipPortraitRadarRsp> shipPortraitRadar(RadarShipPortraitRadarReq req) {
        try {
            NetConnectUtils.RequestParamter<RadarShipPortraitRadarReq> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("雷达船舶画像接口入参{}", JSON.toJSONString(req));
            //设置请求参数
            parameter.setBody(req);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SHIPPORTRAIT_RADAR);
            //设置地址
            parameter.setPath(path);
            ShipPortraitRadarResultVO rsp = connectUtils.post(parameter, ShipPortraitRadarResultVO.class);
            log.info("雷达船舶画像接口返回参数{}", JSON.toJSONString(rsp));
            if (rsp != null && Constants.ZERO.equals(rsp.getCode())) {
                RadarShipPortraitRadarRsp data = rsp.getData();
                return Result.data(data);
            }
            return Result.fail("雷达船舶画像接口请求失败:" + (rsp != null ? rsp.getMsg() : null));
        }
        catch (Exception e) {
            log.info("雷达船舶画像像接口调用异常:", e);
            return Result.fail("雷达船舶画像接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 海图瓦片接口
     */
    public Result<byte[]> getTile(String node, String userName, Integer level,
                        Integer col, Integer row, String skipCode) {
        log.info("getTile node:{}, userName:{}, level:{}, col:{}, row:{} skipCode:{}", node, userName, level, col, row, skipCode);
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_name", userName);
            paramMap.put("level", level);
            paramMap.put("row", row);
            paramMap.put("col", col);
            if (Objects.nonNull(skipCode)) {
                paramMap.put("skipcode", skipCode);
            }

            NetConnectUtils.RequestParamter<Object> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取海图瓦片接口入参{}", JSON.toJSONString(paramMap));
            //设置请求参数
            parameter.setBody(paramMap);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_TILE_RADAR);
            path.setNode(node);
            //设置地址
            parameter.setPath(path);
            ResponseBody responseBody = connectUtils.get4Response(parameter);
            log.info("获取海图瓦片接口返回参数{}", JSON.toJSONString(responseBody));
            return Result.data(responseBody.bytes());
        } catch (Exception e) {
            log.info("雷达获取海图瓦片接口调用异常:", e);
            return Result.fail("雷达获取海图瓦片接口调用异常:" + e.getMessage());
        }
    }

    /**
     * 获取渔区瓦片接口
     */
    public Result<byte[]> getExtendTile(String tileType, String userName, Integer level,
                              Integer col, Integer row) {
        log.info("getExtendTile tileType:{}, userName:{}, level:{}, col:{}, row:{}", tileType, userName, level, col, row);
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_name", userName);
            paramMap.put("level", level);
            paramMap.put("row", row);
            paramMap.put("col", col);
            paramMap.put("tile_type", tileType);

            NetConnectUtils.RequestParamter<Object> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取渔区瓦片接口入参{}", JSON.toJSONString(paramMap));
            //设置请求参数
            parameter.setBody(paramMap);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_EXTEND_TILE_RADAR);
            //设置地址
            parameter.setPath(path);
            ResponseBody responseBody = connectUtils.get4Response(parameter);
            log.info("获取渔区瓦片接口返回参数{}", JSON.toJSONString(responseBody));
            return Result.data(responseBody.bytes());
        } catch (Exception e) {
            log.info("雷达获取渔区瓦片接口调用异常:", e);
            return Result.fail("雷达获取渔区瓦片接口调用异常:" + e.getMessage());
        }
    }
}
