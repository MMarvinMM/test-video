package com.chinatower.video.modules.query.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.event.entity.dto.DevicePointVo;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.layer.GeojsonStruckRes;
import com.chinatower.gis.api.entity.layer.GetXjxlFeaturesByIdsReq;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.artags.dataobject.ArChannelInfo;
import com.chinatower.video.modules.artags.dataobject.ArDeviceInfo;
import com.chinatower.video.modules.artags.mapper.ArTagsInfoMapper;
import com.chinatower.video.modules.artags.service.IArTagsInfoService;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.constants.IndErrorMsgConstants;
import com.chinatower.video.modules.query.controller.VideoQueryController;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.entity.*;
import com.chinatower.video.modules.videocontrol.controller.VideoControlController;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.util.SectorCalculatorUtil;
import com.chinatower.video.util.VideoPositionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频查询服务类
 * Created by ldl on 2022/1/27.
 */
@Service
@Slf4j
public class VideoPtzQueryService {
    @Autowired
    private ApiInfoUtils apiInfoUtils;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    private ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    VideoPtzQueryDAO videoPtzQueryDAO;

    @Autowired
    VideoControlService videoControlService;

    @Autowired
    VideoControlController videoControlController;

    @Autowired
    VideoQueryController videoQueryController;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IArTagsInfoService arTagsInfoService;

    @Autowired
    private ArTagsInfoMapper arTagsInfoMapper;

    @Autowired
    private VideoQueryService videoQueryService;

    @Autowired
    private GisLayerAPI gisLayerAPI;

    private static final double EARTH_RADIUS = 6371000;

    private static final String QUERY_PTZ_FAIL = "查询设备ptz数据请求失败";

    public Result<PresetData> turnToPtzByPointAngle(VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        queryPtzReq.setDeviceCode(videoPtzQueryReq.getDeviceCode());
        queryPtzReq.setChannelCode(videoPtzQueryReq.getChannelCode());
        Result<PresetData> queryPtzRes;
        // 设备ptz值
        PresetData presetData;
        // 转动到ptz接口入参
        DoPtzLocationCmdReq ptzCmdReq = new DoPtzLocationCmdReq();
        // 转动ptz点返回的结果
        Result<T> doPtzCmdRes;
        // 转动角度 P’值
        float pan = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        BigDecimal p0;


        // 转动角度 tilt' 值
        float tilt = 0;
        // 转动角度 tilt' + tilt0值
        float tilt0 = 0;
        BigDecimal t0;

        try{
            // 获取摄像机型号信息
            DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
            if (deviceModel == null) {
                return Result.fail(VideoConstants.TURN_TO_MSG);
            }
            // 获取转动角度
            pan = this.getPan(videoPtzQueryReq);
            tilt = this.getTilt(videoPtzQueryReq,deviceModel);

            // 处理水平角偏差和俯仰角偏差 通过有效参考点进行计算  pan为换算后的P’值，horOffset、pitchOffset为换算后实际的P0和T0
            List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(videoPtzQueryReq.getDeviceCode());
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setRealPan(String.valueOf(pan));
            deviceModelVO = LongLatAngleUtils.getValidOffsetByRealHorAngle(pointsValid, deviceModelVO,new ArrayList<>());
            p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
            pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();

            t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
            tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();

            //p（0-360）值超过360，取余
            if(pan0 >= 360){
                pan0 = pan0 % 360;
            }
            //p（0-360）值小于0，+360
            if(pan0 < 0){
                pan0 = 360 + pan0;
            }

            // 查询当前ptz值
            queryPtzRes = videoQueryController.queryPtzPosition(queryPtzReq);
            if(queryPtzRes.getCode() != 200){
                return Result.fail(QUERY_PTZ_FAIL);
            }
            presetData = queryPtzRes.getData();
            // 定义转到的P值
            ptzCmdReq.setPan(pan0);
            ptzCmdReq.setTilt(tilt0);
            ptzCmdReq.setZoom(presetData.getZoom());
            ptzCmdReq.setDeviceCode(queryPtzReq.getDeviceCode());
            ptzCmdReq.setChannelCode(queryPtzReq.getChannelCode());
            // 调用转动接口
            doPtzCmdRes = videoControlController.doPtzLocationCmd(ptzCmdReq);
            if(doPtzCmdRes.getCode() == 200){
                // 转动后 返回给前端ptz值
                presetData.setPan(pan);
                res = Result.data(presetData);
            }else{
                res = Result.data(400,presetData,doPtzCmdRes.getMsg());
            }
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("根据定位点转动Ptz:" , e);
        }
        return res;
    }

    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算ptz
     *
     * @param videoPtzReq
     * @return VideoPtzRes
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    public VideoPtzRes getPtzByMassPointBatch(VideoPtzReq videoPtzReq)
    {
    	log.info(VideoConstants.GETPTZBYMASSPOINTBATCH + VideoConstants.REQUEST_LOG + JSON.toJSONString(videoPtzReq));
    	// 根据摄像机编码获取有效参考点
    	List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(videoPtzReq.getDeviceCode());
    	if( CollectionUtils.isEmpty(pointsValid) )
    	{
    		throw new CustomException(VideoConstants.POINTS_VALID_ERROR);
    	}
    	// 获取摄像机型号信息
    	VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
    	videoPtzQueryReq.setDeviceCode(videoPtzReq.getDeviceCode());
        DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
        if (deviceModel == null || StringUtils.isEmpty(deviceModel.getDeviceModelCode())) {
        	throw new CustomException(VideoConstants.DEVICE_MODEL_ERROR);
        }
        // 查询当前ptz值
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        queryPtzReq.setDeviceCode(videoPtzReq.getDeviceCode());
        queryPtzReq.setChannelCode(videoPtzReq.getChannelCode());
        Result<PresetData> queryPtzRes = videoQueryController.queryPtzPosition(queryPtzReq);
        if(queryPtzRes.getCode() != 200){
            throw new CustomException(QUERY_PTZ_FAIL);
        }
        PresetData presetData = queryPtzRes.getData();
        VideoPtzRes videoPtzRes = new VideoPtzRes();
        videoPtzRes.setDeviceModel(deviceModel);

        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
        List<SysDictDataResponse> enumList = res.getData();
        List<SysDictData> enumListNew = new ArrayList<>();
        enumList.forEach(item ->{
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item,sysDictData);
            enumListNew.add(sysDictData);
        });

    	// 根据中心点经纬度计算ptz
        LongitudeLatitudeVO centerPoint = videoPtzReq.getCenterPoint();
        if( centerPoint != null )
        {
        	videoPtzRes.setCenterPointPtz(computePtz(deviceModel, pointsValid, centerPoint, presetData.getZoom(), enumListNew));
        }
    	// 根据顶点经纬度计算ptz
        List<LongitudeLatitudeVO> vertexList = videoPtzReq.getVertexList();
        if( CollectionUtils.isNotEmpty(vertexList) )
        {
        	List<PresetData> vertexPtzList = new ArrayList<PresetData>();
        	for( LongitudeLatitudeVO vertex : vertexList )
        	{
        		vertexPtzList.add(computePtz(deviceModel, pointsValid, vertex, presetData.getZoom(),enumListNew));
        	}
        	videoPtzRes.setVertexPtzList(vertexPtzList);
        }
        log.info(VideoConstants.GETPTZBYMASSPOINTBATCH + VideoConstants.RESPONSE_LOG + JSON.toJSONString(videoPtzRes));
    	return videoPtzRes;
    }

    /**
     * 根据地块顶点/中心点的经纬度批量计算ptz
     *
     * @param deviceModel
     * @param pointsValid
     * @param longitudeLatitudeVO
     * @param zoom
     * @return PresetData
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    public PresetData computePtz(DeviceModelVO deviceModel, List<DeviceRefPointVO> pointsValid,
    		LongitudeLatitudeVO longitudeLatitudeVO, float zoom, List<SysDictData> enumListNew)
    {
        log.info("longitudeLatitudeVO=" + JSONObject.toJSONString(longitudeLatitudeVO));
    	// 转动角度 P’值
        float pan = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        BigDecimal p0;
        // 转动角度 tilt' 值
        float tilt = 0;
        // 转动角度 tilt' + tilt0值
        float tilt0 = 0;
        BigDecimal t0;
    	// 把摄像机经纬度和打点经纬度封装到VideoPtzQueryReq对象中
    	VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
    	videoPtzQueryReq.setCameraLon(Double.parseDouble(deviceModel.getLongitude()));
    	videoPtzQueryReq.setCameraLat(Double.parseDouble(deviceModel.getLatitude()));
    	videoPtzQueryReq.setPointLon(longitudeLatitudeVO.getPointLon());
    	videoPtzQueryReq.setPointLat(longitudeLatitudeVO.getPointLat());
    	// 获取转动角度
        pan = this.getPan(videoPtzQueryReq);
        tilt = this.getTilt(videoPtzQueryReq,deviceModel);
        // 处理水平角偏差和俯仰角偏差 通过有效参考点进行计算  pan为换算后的P’值，horOffset、pitchOffset为换算后实际的P0和T0
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(String.valueOf(pan));
        deviceModelVO = LongLatAngleUtils.getValidOffsetByRealHorAngle(pointsValid, deviceModelVO,enumListNew);
        p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
        pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();

        t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
        tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();

        //p（0-360）值超过360，取余
        if(pan0 >= 360){
            pan0 = pan0 % 360;
        }
        //p（0-360）值小于0，+360
        if(pan0 < 0){
            pan0 = 360 + pan0;
        }
        // 返回结果对象
        PresetData presetData = new PresetData();
        presetData.setPan(pan0);
        presetData.setTilt(tilt0);
        presetData.setZoom(zoom);
        presetData.setPointLon(longitudeLatitudeVO.getPointLon());
        presetData.setPointLat(longitudeLatitudeVO.getPointLat());
        return presetData;
    }

    public Result<PresetData> turnToPtzByPointAngle2(VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        queryPtzReq.setDeviceCode(videoPtzQueryReq.getDeviceCode());
        queryPtzReq.setChannelCode(videoPtzQueryReq.getChannelCode());
        queryPtzReq.setTenantId(videoPtzQueryReq.getTenantId());
        Result<PresetData> queryPtzRes;
        // 设备ptz值
        PresetData presetData;
        // 转动到ptz接口入参
        DoPtzLocationCmdReq ptzCmdReq = new DoPtzLocationCmdReq();
        // 转动ptz点返回的结果
        Result<T> doPtzCmdRes;
        // 转动角度 P’值
        float pan = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        BigDecimal p0;


        // 转动角度 tilt' 值
        float tilt = 0;
        // 转动角度 tilt' + tilt0值
        float tilt0 = 0;
        BigDecimal t0;

        try{
            // 获取摄像机型号信息
            DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
            if (deviceModel == null) {
                return Result.fail(VideoConstants.TURN_TO_MSG);
            }
            // 获取转动角度
            pan = this.getPan(videoPtzQueryReq);
            tilt = this.getTilt(videoPtzQueryReq,deviceModel);

            // 处理水平角偏差和俯仰角偏差 通过有效参考点进行计算  pan为换算后的P’值，horOffset、pitchOffset为换算后实际的P0和T0
            List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(videoPtzQueryReq.getDeviceCode());
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setRealPan(String.valueOf(pan));
            deviceModelVO = LongLatAngleUtils.getValidOffsetByRealHorAngle(pointsValid, deviceModelVO,new ArrayList<>());
            p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
            pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();

            t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
            tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();

            //p（0-360）值超过360，取余
            if(pan0 >= 360){
                pan0 = pan0 % 360;
            }
            //p（0-360）值小于0，+360
            if(pan0 < 0){
                pan0 = 360 + pan0;
            }

            // 查询当前ptz值
            queryPtzRes = videoQueryController.queryPtzPosition2(queryPtzReq);
            if(queryPtzRes.getCode() != 200){
                return Result.fail(QUERY_PTZ_FAIL);
            }
            presetData = queryPtzRes.getData();
            // 定义转到的P值
            ptzCmdReq.setPan(pan0);
            ptzCmdReq.setTilt(tilt0);
            ptzCmdReq.setZoom(presetData.getZoom());
            ptzCmdReq.setDeviceCode(queryPtzReq.getDeviceCode());
            ptzCmdReq.setChannelCode(queryPtzReq.getChannelCode());
            ptzCmdReq.setTenantId(videoPtzQueryReq.getTenantId());
            ptzCmdReq.setUserId(videoPtzQueryReq.getUserId());
            ptzCmdReq.setFromSource(VideoConstants.FROM_WEICHUANG);
            // 调用转动接口
            doPtzCmdRes = videoControlController.doPtzLocationCmd2(ptzCmdReq);
            if(doPtzCmdRes.getCode() == 200){
                // 转动后 返回给前端ptz值
                presetData.setPan(pan);
                res = Result.data(presetData);
            }else{
                res = Result.data(400,presetData,doPtzCmdRes.getMsg());
            }
        }catch (Exception e){
            res = Result.fail("获取摄像机信息有误");
            log.error("根据定位点转动Ptz:" , e);
        }
        return res;
    }

    public Result<List<PresetData>> focusThisPoint(FocusThisPointReq focusThisPointReq) {
        Result<List<PresetData>> res;
        LoginUser currentUser = SecurityUtils.getLoginUser();
        focusThisPointReq.setCustomerCode(currentUser.getUser().getTenantId());
        // 符合条件的所有设备
        List<DeviceModelVO> deviceList;
        List<PresetData> presetDataList = new ArrayList<>();
        // 查询设备角度参数
        VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
        videoPtzQueryReq.setPointLon(focusThisPointReq.getPointLon());
        videoPtzQueryReq.setPointLat(focusThisPointReq.getPointLat());
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        // 设备ptz值
        PresetData presetData;
        // 转动到ptz接口入参
        DoPtzLocationCmdReq ptzCmdReq = new DoPtzLocationCmdReq();
        // 转动ptz点返回的结果
        Result<T> doPtzCmdRes;
        // 转动角度 P’值
        float pan = 0;
        // 俯视角度 T’值
        float tilt = 0;
        // 转动角度 Z’值
        float zoom = 1;
        // 转动角度 P’+P0值
        float pan0 = 0;
        // 俯视角度 T’+T0值
        float tilt0 = 0;
        // 换算后厂商的P值
        float companyPan = 0;
        // 换算后厂商的T值
        float companyTilt = 0;
        // 换算后厂商的Z值
        float companyZoom = 0;
        BigDecimal p0;
        BigDecimal t0;
        try{
            deviceList = videoPtzQueryDAO.getDeviceList(focusThisPointReq);
            // for 循环优化
            Map<String, List<DeviceRefPointVO>> deviceCodeToPointsMap = new HashMap<>();
            for (DeviceModelVO deviceModel: deviceList) {
                List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValid(deviceModel.getDeviceCode());
                deviceCodeToPointsMap.put(deviceModel.getDeviceCode(), pointsValid);
            }
            for (DeviceModelVO deviceModel: deviceList) {
                videoPtzQueryReq.setCameraLon(new Double(deviceModel.getLongitude()));
                videoPtzQueryReq.setCameraLat(new Double(deviceModel.getLatitude()));
                // 获取转动角度
                pan = queryP(videoPtzQueryReq);
                // 获取转动角度
                tilt = queryT(deviceModel);

                // 处理水平角偏差和俯仰角偏差   通过有效参考点进行计算
                List<DeviceRefPointVO> pointsValid = deviceCodeToPointsMap.get(deviceModel.getDeviceCode());
                DeviceModelVO deviceModelVO = new DeviceModelVO();
                deviceModelVO.setRealPan(String.valueOf(pan));
                List<SysDictData> enumListNew = new ArrayList<>();
                deviceModelVO = LongLatAngleUtils.getValidOffset(pointsValid, deviceModelVO,enumListNew);
                p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
                t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
                pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();
                tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();
                if(pan0 < 0){
                    pan0 = 360 + pan0;
                }
                // 根据型号和偏差值换算设备识别的p值
                companyPan = Float.parseFloat(LongLatAngleUtils.paramTransReverse(String.valueOf(pan0), deviceModel, "P"));
                // 根据型号和偏差值换算设备识别的p值
                companyTilt = Float.parseFloat(LongLatAngleUtils.paramTransReverse(String.valueOf(tilt0), deviceModel, "T"));
                // 根据型号和偏差值换算设备识别的p值
                companyZoom = Float.parseFloat(LongLatAngleUtils.paramTransReverse(String.valueOf(zoom), deviceModel, "Z"));

                companyPan = BigDecimal.valueOf(companyPan).add(p0).floatValue();
                companyTilt = BigDecimal.valueOf(companyTilt).add(t0).floatValue();

                // 定义转到的P值
                ptzCmdReq.setPan(companyPan);
                ptzCmdReq.setTilt(companyTilt);
                ptzCmdReq.setZoom(companyZoom);
                ptzCmdReq.setDeviceCode(queryPtzReq.getDeviceCode());
                ptzCmdReq.setChannelCode(queryPtzReq.getChannelCode());
                // 调用转动接口
                doPtzCmdRes = videoControlController.doPtzLocationCmd(ptzCmdReq);
                if(doPtzCmdRes.getCode() == 200){
                    presetData = new PresetData();
                    // 转动后 返回给前端ptz值
                    presetData.setPan(pan);
                    presetData.setTilt(tilt);
                    presetData.setZoom(zoom);
                    presetDataList.add(presetData);
                }
            }
            res = Result.data(presetDataList);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("focusThisPoint根据定位点转动Ptz:" , e);
        }
        return res;
    }
    public Result<PresetDetailData> getCompass(VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetDetailData> res;
        PresetDetailData presetDetailData = new PresetDetailData();
        // 查询设备角度参数
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        // 设备ptz值
        PresetData presetData;
        // 转动角度 P’值
        float pan = 0;
        // 转动角度 T’值
        float tilt = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        String pan0Str;
        // 俯视角度 T’+T0值
        float tilt0 = 0;
        String tilt0Str;
        float zoom = 0;
        String zoomStr;
        DeviceModelVO deviceModel;
        Result<PresetData> presetDataRes;
        BigDecimal p0;
        BigDecimal t0;
        try{
            deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
            if(null == deviceModel){
                return Result.fail("指北针功能无法使用，请联系铁塔管理员配置摄像机厂家和型号。");
            }
            queryPtzReq.setDeviceCode(videoPtzQueryReq.getDeviceCode());
            queryPtzReq.setChannelCode(videoPtzQueryReq.getChannelCode());
            // 查询当前ptz值
            presetDataRes = videoQueryController.queryPtzPosition(queryPtzReq);
            if(presetDataRes.getCode() != 200){
                return Result.fail(QUERY_PTZ_FAIL);
            }
            presetData = presetDataRes.getData();
            // 获取水平真实角度
//            pan0Str = LongLatAngleUtils.paramTrans(String.valueOf(presetData.getPan()), deviceModel, "P");
            pan0 = new BigDecimal(presetData.getPan()).setScale( 0, BigDecimal.ROUND_HALF_UP ).floatValue();
//            tilt0Str = LongLatAngleUtils.paramTrans(String.valueOf(presetData.getTilt()), deviceModel, "T");
            tilt0 = new BigDecimal(presetData.getTilt()).setScale( 0, BigDecimal.ROUND_HALF_UP ).floatValue();
//            zoomStr = LongLatAngleUtils.paramTrans(String.valueOf(presetData.getZoom()), deviceModel, "Z");
            zoom = new BigDecimal(presetData.getZoom()).setScale( 0, BigDecimal.ROUND_HALF_UP ).floatValue();

            // 处理水平角偏差和俯仰角偏差   通过有效参考点进行计算
            List<DeviceRefPointVO> pointsValid= videoPtzQueryDAO.getDevicePointsValid(videoPtzQueryReq.getDeviceCode());
            if(CollectionUtils.isNotEmpty(pointsValid) &&  pointsValid.size() >0){
                Result<List<SysDictDataResponse>> sysRes = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
                List<SysDictDataResponse> enumList = sysRes.getData();
                List<SysDictData> enumListNew = new ArrayList<>();
                enumList.forEach(item ->{
                    SysDictData sysDictData = new SysDictData();
                    BeanUtils.copyProperties(item,sysDictData);
                    enumListNew.add(sysDictData);
                });

                DeviceModelVO deviceModelVo = new DeviceModelVO();
                deviceModelVo.setRealPan(String.valueOf(presetData.getPan()));
                DeviceModelVO deviceModelVoRel = LongLatAngleUtils.getValidOffset(pointsValid, deviceModelVo,enumListNew);
                p0 = deviceModelVoRel.getP0();
                t0 = deviceModelVoRel.getT0();
            }else{
                p0 = BigDecimal.valueOf(0);
                t0 = BigDecimal.valueOf(0);
            }
            pan = BigDecimal.valueOf(pan0).subtract(p0).intValue();
            tilt = BigDecimal.valueOf(tilt0).subtract(t0).intValue();
            if(pan > 360){
                pan = (int)(pan - 360);
            }else if(pan < 0){
                pan = (int)(pan + 360);
            }
            presetDetailData.setPan(pan);
            presetDetailData.setTilt(tilt);
            presetDetailData.setZoom(zoom);
            // f= f0*z'
            BigDecimal realFocal = new BigDecimal(deviceModel.getMinFocus()).multiply(BigDecimal.valueOf(zoom)).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigDecimalTwo = BigDecimal.valueOf(2);
            BigDecimal doubleRealFocal = bigDecimalTwo.multiply(realFocal);
            // 防止除法报错
            if(doubleRealFocal.compareTo(BigDecimal.ZERO) == 0){
                doubleRealFocal = BigDecimal.valueOf(1);
            }
            if(deviceModel.getPicSensorLength() != null) {
                // 2*arctan(c/2f)
                double c2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorLength()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                presetDetailData.setViewableAngle(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(c2f)).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue());
            }else{
                presetDetailData.setViewableAngle(60);
            }
            log.info("trans：{}", JSON.toJSONString(presetDetailData));
            res = Result.data(presetDetailData);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("指北针接口异常:" , e);
        }
        return res;
    }

    /**
     * 查询转动角度
     * @param videoPtzQueryReq
     * @return
     */
    private float queryP(VideoPtzQueryReq videoPtzQueryReq) {
        double pAngle = Math.atan2(videoPtzQueryReq.getPointLon() - videoPtzQueryReq.getCameraLon(), videoPtzQueryReq.getPointLat() - videoPtzQueryReq.getCameraLat());
        pAngle = Math.toDegrees(pAngle);
        return pAngle < 0 ? 360 + (int)Math.round(pAngle): (int)Math.round(pAngle);
    }

    /**
     * 查询俯仰角度
     * @param deviceModel
     * @return
     */
    private float queryT(DeviceModelVO deviceModel) {
        if(deviceModel.getDistance() == 0){
            return 1;
        }
        double tAngle = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getHeight()).divide(BigDecimal.valueOf(deviceModel.getDistance()), 4, BigDecimal.ROUND_HALF_UP).doubleValue()));
        return (int)Math.round(tAngle);
    }

    /**
     * 参数进制转换
     * PTZ逆转换
     * 假设：接口获取的值为PTZ，转义后的P’T’Z’。已知P’T’Z’，求PTZ。
     * (一)	海康威视
     * P’T’Z’乘以10，作为16进制转为10进制。
     * (二)	大华
     * P=P’*10。
     * T= T’ *10。
     * Z= Z’*128/（最大焦距/最小焦距）。
     * @param paramValue
     * @param deviceModel
     * @param type
     * @return
     */
    public float paramTransToComputer(float paramValue, DeviceModel deviceModel, String type) {
        float resultValue = paramValue;
        if (DeviceModel.MODELID_DAHUA.equals(deviceModel.getMfgrCode()) || DeviceModel.MODELID_DHUA.equals(deviceModel.getMfgrCode())) {
            if("P".equals(type)){
                resultValue = (360 - (int)paramValue) * 10;
            }else if("T".equals(type)){
                resultValue = paramValue * 10;
            }
        }else if(DeviceModel.MODELID_HIKVISION.equals(deviceModel.getMfgrCode()) && ("P".equals(type) || "T".equals(type))){
            resultValue = Integer.parseInt(String.valueOf((int)(paramValue*10)), 16);
        }
        // 根据配置对t值进行翻转转换 水平向下为负 0否1是
        if ("T".equals(type) && !DeviceConstants.YES_FLAG.equals(deviceModel.getVertiDownNeg())) {
            resultValue = resultValue * -1;
        }
        if("Z".equals(type)) {
            // 最大放大倍率
            float maxRate = 1;
            if(StringUtils.isNotBlank(deviceModel.getFocusRange())){
                String[] focusRange = deviceModel.getFocusRange().split("~");
                if (focusRange.length >= 2) {
                    BigDecimal minFocus = new BigDecimal(focusRange[0]);
                    BigDecimal maxFocus = new BigDecimal(focusRange[1]);
                    maxRate = maxFocus.divide(minFocus).setScale( 0, BigDecimal.ROUND_HALF_UP ).floatValue();
                }
                resultValue = Float.min(Float.max((new BigDecimal(deviceModel.getMinFocus()).setScale( 2, BigDecimal.ROUND_HALF_UP )).floatValue(),maxRate * deviceModel.getDistance() / 200),maxRate * 5);
            }
        }
        log.info("paramTransToComputer 参数进制转换：paramValue：{},deviceModel：{},type：{},resultValue：{}", paramValue, JSON.toJSONString(deviceModel), type, resultValue);
        return resultValue;
    }

    // 平均半径,单位：m；不是赤道半径。赤道为6378左右
    public static double getDistance(Double lat1,Double lng1,Double lat2,Double lng2) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(lng1); // A经弧度
        double radiansAY = Math.toRadians(lat1); // A纬弧度
        double radiansBX = Math.toRadians(lng2); // B经弧度
        double radiansBY = Math.toRadians(lat2); // B纬弧度

        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
        double acos = Math.acos(cos); // 反余弦值
        return EARTH_RADIUS * acos; // 最终结果
    }

    /**
     * 查询真实水平
     * @return
     */
    private float getPan(VideoPtzQueryReq videoPtzQueryReq){
        //原点：x1,y1 经度,纬度
        double x1 = videoPtzQueryReq.getCameraLon();
        double y1 = videoPtzQueryReq.getCameraLat();

        //参考点：x2,y2 经度,纬度
        double x2 = videoPtzQueryReq.getPointLon();
        double y2 = videoPtzQueryReq.getPointLat();

        //ad：x2,y1
        double ad = getDistance(y1, x1, y1, x2);
        //ab：x1,y2
        double ab = getDistance(y1, x1, y2, x1);

        log.info("ad=="+ad);
        log.info("ab=="+ab);
        double pan = 0;
        if(ad == 0 || ab == 0){
            if(x2 == x1 && y2 >= y1){
                pan = 0;
            }

            if(x2 == x1 && y2 < y1){
                pan = 180;
            }

            if(x2 > x1 && y2 == y1){
                pan = 90;
            }

            if(x2 < x1 && y2 == y1){
                pan = 270;
            }
        }else{
            pan = Math.abs(Math.atan(ad / ab) * 180 / Math.PI);

            if(x2 > x1 && y2 < y1){
                pan = 180 - pan;
            }

            if(x2 < x1 && y2 < y1){
                pan = 180 + pan;
            }

            if(x2 < x1 && y2 > y1){
                pan = 360 - pan;
            }
        }

        //保留两位小数
        return new Double(String.format("%.2f",pan)).floatValue();
    }

    /**
     * 查询真实俯仰角
     * @return
     */
    private float getTilt(VideoPtzQueryReq videoPtzQueryReq,DeviceModelVO deviceModel){
        double distance = getDistance(videoPtzQueryReq.getCameraLat(), videoPtzQueryReq.getCameraLon(),videoPtzQueryReq.getPointLat(), videoPtzQueryReq.getPointLon());
        double tilt = -1 * Math.abs(Math.atan(Double.parseDouble(deviceModel.getHeight()) / distance) * 180 / Math.PI);
        //保留两位小数
        return new Double(String.format("%.2f",tilt)).floatValue();
    }

    public VideoPtzRes getPercentByPtzBatch(VideoPtzRes videoPtzRes) {
        log.info("getPercentByPtzBatch入参:{}",JSON.toJSONString(videoPtzRes));
        List<PercentVO> vertexPercentList = new ArrayList<>();
        //顶点坐标
        List<PresetData> vertexPtzList = videoPtzRes.getVertexPtzList();
        //中心点坐标
        PresetData centerPointPtz = videoPtzRes.getCenterPointPtz();
        BigDecimal p0 = new BigDecimal(centerPointPtz.getPan());
        BigDecimal t0 = new BigDecimal(centerPointPtz.getTilt());
        float z0 = centerPointPtz.getZoom();
        if(CollectionUtils.isNotEmpty(vertexPtzList)){
            DeviceModelVO deviceModel = videoPtzRes.getDeviceModel();
            if(!ObjectUtil.isEmpty(deviceModel)){
                double f0 = Double.valueOf(deviceModel.getMinFocus());
                double c = Double.valueOf(deviceModel.getPicSensorLength());
                double d = Double.valueOf(deviceModel.getPicSensorWidth());
                //水平视场角和垂直视场角计算
                double f = f0 * Double.valueOf(z0);
                BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
                BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));

                for (PresetData presetData : vertexPtzList) {
                    BigDecimal pan = new BigDecimal(presetData.getPan());
                    BigDecimal tilt = new BigDecimal(presetData.getTilt());

                    double panP0 = pan.subtract(p0).doubleValue();

                    if(panP0 >= 360){
                        panP0 = panP0 % 360;
                    }
                    //p（0-360）值小于0，+360
                    if(panP0 < 0){
                        panP0 = 360 + panP0;
                    }

                    double abs = 360 - panP0;
                    double min = panP0;

                    if(panP0 > abs){
                        min = -abs;
                    }

                    BigDecimal subtract = t0.subtract(tilt);
                    if(Constants.ONE.equals(deviceModel.getVertiDownNeg())){
                        subtract = subtract.negate();
                    }

                    //计算百分比坐标
                    BigDecimal u = (BigDecimal.valueOf(min)).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
                    BigDecimal v = (subtract).divide(b,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));

                    PercentVO percentVO = new PercentVO();
                    percentVO.setPercentU(String.valueOf(u));
                    percentVO.setPercentV(String.valueOf(v));
                    percentVO.setPointLon(presetData.getPointLon());
                    percentVO.setPointLat(presetData.getPointLat());
                    vertexPercentList.add(percentVO);
                }
            }
        }
        videoPtzRes.setVertexPercentList(vertexPercentList);
        //不再回传给前端
        videoPtzRes.setVertexPtzList(null);
        videoPtzRes.setDeviceModel(null);
        log.info("getPercentByPtzBatch出参:{}", JSON.toJSONString(videoPtzRes));
        return videoPtzRes;
    }

    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算顶点的百分比坐标和中心点的ptz
     *
     * @param videoPtzReq
     * @return VideoPtzRes
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    public VideoPtzRes getPercentPtzByMassPointBatch(VideoPtzReq videoPtzReq)
    {
    	VideoPtzRes res = getPtzByMassPointBatch(videoPtzReq);
    	VideoPtzRes result = getPercentByPtzBatch(res);
    	return result;
    }

    /**
     * 视图联动，判断该摄像机是否有有效参考点
     *
     * @param videoPtzReq
     * @return Result<String>
     * @author wanglei
     * @date 2023/08/23
     * @version release_v1.6.10
     */
    public Result<String> pointsValid(VideoPtzReq videoPtzReq)
    {
    	log.info(VideoConstants.POINTS_VALID + VideoConstants.REQUEST_LOG + JSON.toJSONString(videoPtzReq));
    	// 根据摄像机编码获取有效参考点
    	List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValid(videoPtzReq.getDeviceCode());
    	log.info(VideoConstants.POINTS_VALID + VideoConstants.RESPONSE_LOG + JSON.toJSONString(pointsValid));
    	if( CollectionUtils.isEmpty(pointsValid) )
    	{
    		return Result.fail(VideoConstants.POINTS_VALID_ERROR);
    	}
    	else
    	{
    		return Result.success(VideoConstants.POINTS_VALID_SUCC);
    	}
    }

    public Result<List<DeviceRefPointVO>> pointsValidSuccess(VideoPtzReq videoPtzReq)
    {
        // 获取主视频画面摄像机摄像机型号信息
        List<DeviceModelDTO> deviceModelList = videoPtzQueryDAO.getDeviceModelForArMove(videoPtzReq.getDeviceCode());
        DeviceModelDTO deviceModel = deviceModelList.get(Constants.ZERO_NUM);
        if (StringUtils.isBlank(deviceModel.getMinFocus())
                || StringUtils.isBlank(deviceModel.getPicSensorLength())
                ||StringUtils.isBlank(deviceModel.getPicSensorWidth())
                ||StringUtils.isBlank(deviceModel.getHeight())
        ) {
            return Result.data(new ArrayList<>());
        }
        // 根据摄像机编码获取有效参考点
        return Result.data(videoPtzQueryDAO.getDevicePointsValid(videoPtzReq.getDeviceCode()));
    }

    /**
     * 批量判断摄像机是否有有效参考点
     *
     * @param list
     * @return Result<List<DevicePointsValidVO>>
     * @author hued
     * @date 2023年9月14日09:48:33
     * @version release_v1.6.10
     */
    public Result<List<DevicePointsValidVO>> pointsValidBatch(List<String> list)
    {
        List<DevicePointsValidVO> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            log.info(VideoConstants.POINTS_VALID_BATCH + VideoConstants.REQUEST_LOG + JSON.toJSONString(list));
            // 根据摄像机编码获取有效参考点
            List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidBatch(list);
            log.info(VideoConstants.POINTS_VALID_BATCH + VideoConstants.RESPONSE_LOG + JSON.toJSONString(pointsValid));

            Map<String, List<DeviceRefPointVO>> map = pointsValid.stream().collect(Collectors.groupingBy(DeviceRefPointVO::getDeviceCode));


            list.forEach(deviceCode -> {
                DevicePointsValidVO devicePointsValidVO = new DevicePointsValidVO();
                devicePointsValidVO.setDeviceCode(deviceCode);
                if(ObjectUtil.isEmpty(map.get(deviceCode))){
                    //0:未标定  1已标定
                    devicePointsValidVO.setIsPointsValid(Constants.ZERO);
                }else{
                    //0:未标定  1已标定
                    devicePointsValidVO.setIsPointsValid(Constants.ONE);
                }
                resultList.add(devicePointsValidVO);
            });

        }
        return Result.data(resultList);
    }

    public Result<PresetData> turnToPtzByPointAngleFly(VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        queryPtzReq.setDeviceCode(videoPtzQueryReq.getDeviceCode());
        queryPtzReq.setChannelCode(videoPtzQueryReq.getChannelCode());
        Result<PresetData> queryPtzRes;

        // 转动ptz点返回的结果
        Result<T> doPtzCmdRes;
        // 转动角度 P’值
        float pan = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        BigDecimal p0;


        // 转动角度 tilt' 值
        float tilt = 0;
        // 转动角度 tilt' + tilt0值
        float tilt0 = 0;
        BigDecimal t0;

        try{
            // 获取摄像机型号信息
            DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
            if (deviceModel == null) {
                return Result.fail(VideoConstants.TURN_TO_MSG);
            }
            // 获取转动角度
            pan = this.getPan(videoPtzQueryReq);
            tilt = this.getTiltFly(videoPtzQueryReq,deviceModel);

            // 处理水平角偏差和俯仰角偏差 通过有效参考点进行计算  pan为换算后的P’值，horOffset、pitchOffset为换算后实际的P0和T0
            List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(videoPtzQueryReq.getDeviceCode());
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setRealPan(String.valueOf(pan));
            deviceModelVO = LongLatAngleUtils.getValidOffsetByRealHorAngle(pointsValid, deviceModelVO,new ArrayList<>());
            p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
            pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();

            t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
            tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();

            //p（0-360）值超过360，取余
            if(pan0 >= 360){
                pan0 = pan0 % 360;
            }
            //p（0-360）值小于0，+360
            if(pan0 < 0){
                pan0 = 360 + pan0;
            }

            // 查询当前ptz值
            queryPtzRes = videoQueryController.queryPtzPosition(queryPtzReq);
            if(queryPtzRes.getCode() != 200){
                return Result.fail(QUERY_PTZ_FAIL);
            }
            // 设备ptz值
            PresetData presetData = queryPtzRes.getData();
            presetData.setPan(pan0);
            presetData.setTilt(tilt0);
//            presetData.setPan(Float.valueOf(LongLatAngleUtils.paramTransReverse(String.valueOf(pan0), deviceModel, "P")));
//            presetData.setTilt(Float.valueOf(LongLatAngleUtils.paramTransReverse(String.valueOf(tilt0), deviceModel, "T")));
            res = Result.data(presetData);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("根据定位点+无人机飞行高度，计算ptz:" , e);
        }
        return res;
    }

    /**
     * 查询真实俯仰角（摄像机挂高-无人机飞行高度）
     * @return
     */
    private float getTiltFly(VideoPtzQueryReq videoPtzQueryReq,DeviceModelVO deviceModel){
        double distance = getDistance(videoPtzQueryReq.getCameraLat(), videoPtzQueryReq.getCameraLon(),videoPtzQueryReq.getPointLat(), videoPtzQueryReq.getPointLon());
        //摄像机挂高-无人机飞行高度
        double t = Math.abs(Double.parseDouble(deviceModel.getHeight()) - Double.parseDouble(videoPtzQueryReq.getFlyHeight()));
        double tilt = -1 * Math.abs(Math.atan(t / distance) * 180 / Math.PI);
        //保留两位小数
        return new Double(String.format("%.2f",tilt)).floatValue();
    }

    public VideoPtzRes getPercentPtzByMassPointBatchNew(VideoPtzReq videoPtzReq)
    {
        VideoPtzRes res = this.getPtzByMassPointBatchNew(videoPtzReq);
        return this.getPercentByPtzBatchNew(res);
    }

    public VideoPtzRes getPtzByMassPointBatchNew(VideoPtzReq videoPtzReq)
    {
        log.info(VideoConstants.GETPTZBYMASSPOINTBATCH + VideoConstants.REQUEST_LOG + JSON.toJSONString(videoPtzReq));
        // 根据摄像机编码获取有效参考点
        List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(videoPtzReq.getDeviceCode());
        if( CollectionUtils.isEmpty(pointsValid) )
        {
            throw new CustomException(VideoConstants.POINTS_VALID_ERROR);
        }
        // 获取摄像机型号信息
        VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
        videoPtzQueryReq.setDeviceCode(videoPtzReq.getDeviceCode());
        DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
        if (deviceModel == null || StringUtils.isEmpty(deviceModel.getDeviceModelCode())) {
            throw new CustomException(VideoConstants.DEVICE_MODEL_ERROR);
        }
        // 查询当前ptz值
        VideoBaseReq queryPtzReq = new VideoBaseReq();
        queryPtzReq.setDeviceCode(videoPtzReq.getDeviceCode());
        queryPtzReq.setChannelCode(videoPtzReq.getChannelCode());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,videoPtzReq.getTenantId());
        Result<PresetData> queryPtzRes = videoQueryService.queryPtzPosition(queryPtzReq, headerMap);
        if(queryPtzRes.getCode() != 200){
            throw new CustomException(QUERY_PTZ_FAIL);
        }
        PresetData presetData = queryPtzRes.getData();
        VideoPtzRes videoPtzRes = new VideoPtzRes();
        videoPtzRes.setDeviceModel(deviceModel);
        // 根据中心点经纬度计算ptz
        PresetData centerPointPtz = new PresetData();
        centerPointPtz.setPan(presetData.getPan());
        centerPointPtz.setTilt(presetData.getTilt());
        centerPointPtz.setZoom(presetData.getZoom());
        videoPtzRes.setCenterPointPtz(centerPointPtz);
        // 根据顶点经纬度计算ptz
        List<LongitudeLatitudeVO> vertexList = videoPtzReq.getVertexList();
        if( CollectionUtils.isNotEmpty(vertexList) )
        {
            Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
            List<SysDictDataResponse> enumList = res.getData();
            List<SysDictData> enumListNew = new ArrayList<>();
            if(!CollectionUtils.isEmpty(enumList)){
                enumList.forEach(item ->{
                    SysDictData sysDictData = new SysDictData();
                    BeanUtils.copyProperties(item,sysDictData);
                    enumListNew.add(sysDictData);
                });
            }
            List<PresetData> vertexPtzList = new ArrayList<PresetData>();
            for( LongitudeLatitudeVO vertex : vertexList )
            {
                PresetData presetData1 = this.computePtz(deviceModel, pointsValid, vertex, presetData.getZoom(),enumListNew);
                presetData1.setId(vertex.getId());
                vertexPtzList.add(presetData1);
            }
            videoPtzRes.setVertexPtzList(vertexPtzList);
        }
        log.info(VideoConstants.GETPTZBYMASSPOINTBATCH + VideoConstants.RESPONSE_LOG + JSON.toJSONString(videoPtzRes));
        return videoPtzRes;
    }


    public VideoPtzRes getPercentByPtzBatchNew(VideoPtzRes videoPtzRes) {
        log.info("getPercentByPtzBatch入参:{}",JSON.toJSONString(videoPtzRes));
        List<PercentVO> vertexPercentList = new ArrayList<>();
        //顶点坐标
        List<PresetData> vertexPtzList = videoPtzRes.getVertexPtzList();
        //中心点坐标
        PresetData centerPointPtz = videoPtzRes.getCenterPointPtz();
        BigDecimal p0 = new BigDecimal(centerPointPtz.getPan());
        BigDecimal t0 = new BigDecimal(centerPointPtz.getTilt());
        float z0 = centerPointPtz.getZoom();
        if(CollectionUtils.isNotEmpty(vertexPtzList)){
            DeviceModelVO deviceModel = videoPtzRes.getDeviceModel();
            if(!ObjectUtil.isEmpty(deviceModel)){
                double f0 = Double.valueOf(deviceModel.getMinFocus());
                double c = Double.valueOf(deviceModel.getPicSensorLength());
                double d = Double.valueOf(deviceModel.getPicSensorWidth());
                //水平视场角和垂直视场角计算
                double f = f0 * Double.valueOf(z0);
                BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
                BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));

                for (PresetData presetData : vertexPtzList) {
                    BigDecimal pan = new BigDecimal(presetData.getPan());
                    BigDecimal tilt = new BigDecimal(presetData.getTilt());
                    double panP0 = pan.subtract(p0).doubleValue();

                    if(panP0 >= 360){
                        panP0 = panP0 % 360;
                    }
                    //p（0-360）值小于0，+360
                    if(panP0 < 0){
                        panP0 = 360 + panP0;
                    }

                    double abs = 360 - panP0;
                    double min = panP0;
                    if(panP0 > abs){
                        min = -abs;
                    }

                    BigDecimal subtract = t0.subtract(tilt);
                    if(Constants.ONE.equals(deviceModel.getVertiDownNeg())){
                        subtract = subtract.negate();
                    }

                    //计算百分比坐标
                    BigDecimal u = (BigDecimal.valueOf(min)).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
                    BigDecimal v = (subtract).divide(b,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
                    log.info("u:{},v:{}", u, v);
                    PercentVO percentVO = new PercentVO();
                    percentVO.setId(presetData.getId());
                    percentVO.setPercentU(String.valueOf(u));
                    percentVO.setPercentV(String.valueOf(v));
                    percentVO.setPointLon(presetData.getPointLon());
                    percentVO.setPointLat(presetData.getPointLat());
                    vertexPercentList.add(percentVO);
                }
            }
        }
        videoPtzRes.setVertexPercentList(vertexPercentList);
        //不再回传给前端
        videoPtzRes.setDeviceModel(null);
        log.info("getPercentByPtzBatch出参:{}", JSON.toJSONString(videoPtzRes));
        return videoPtzRes;
    }

    public Result<List<ArDeviceInfo>> getArTagMoveList(ArTagMoveReq arTagMoveReq){
        String threadId = IdUtils.fastSimpleUUID();
        long time = new Date().getTime();
        log.info(threadId + "-getArTagMoveList开始，设备{}，通道{}",arTagMoveReq.getDeviceCode(),arTagMoveReq.getChannelCode());
        List<ArDeviceInfo> result = new ArrayList<>();
        try{
            //0:不按百分比显示 1:按百分比显示
            String isPercentShow = arTagMoveReq.getIsPercentShow();
            if(Constants.ONE.equals(isPercentShow)){
                result = this.getArTagMoveListOld(arTagMoveReq, threadId);
            }else {
                result = this.getArTagMoveListNew(arTagMoveReq, threadId);
            }
        }catch (Exception e){
            log.error(threadId + "-getArTagMoveList异常", e);
        }
        log.info(threadId + "-getArTagMoveList结束，耗时" + (new Date().getTime() - time) +"ms");
        return Result.data(result);
    }

    private List<ArDeviceInfo> getArTagMoveListNew(ArTagMoveReq arTagMoveReq, String threadId){
        List<ArDeviceInfo> result = new ArrayList<>();
        //入参标签
        List<ArTagMoveVO> arTagInfoReqList = arTagMoveReq.getArTagInfoReqList();
        if(CollectionUtils.isEmpty(arTagInfoReqList)){
            return result;
        }
        //查询用户记忆的标签
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(CollectionUtils.isNotEmpty(arTagInfoReqList)){
            // 获取主视频画面摄像机摄像机型号信息
            List<DeviceModelDTO> deviceModelList = videoPtzQueryDAO.getDeviceModelForArMove(arTagMoveReq.getDeviceCode());
            DeviceModelDTO deviceModel = deviceModelList.get(Constants.ZERO_NUM);
            if (StringUtils.isBlank(deviceModel.getMinFocus())
                    || StringUtils.isBlank(deviceModel.getPicSensorLength())
                    ||StringUtils.isBlank(deviceModel.getPicSensorWidth())
                    ||StringUtils.isBlank(deviceModel.getHeight())
            ) {
                throw new CustomException("摄像机型号信息错误！");
            }

            // 查询主视频画面摄像机通道的PTZ
            long time2 = new Date().getTime();
            ArTagMovePtzVO ptzPosition = this.queryPtzPosition(arTagMoveReq, deviceModel);
            log.info(threadId + "-deviceCode:{},trans p:{},t:{},z:{}", arTagMoveReq.getDeviceCode(),ptzPosition.getTransPan(), ptzPosition.getTransTilt(), ptzPosition.getTransZoom());
            log.info(threadId + "-queryPtzPosition耗时" + (new Date().getTime() - time2) +"ms");

            // 查询主视频画面摄像机通道的可视域
            this.getDeviceVisibleByOne(deviceModel, ptzPosition);

            // 计算可视域扇形区域的经纬度坐标集合
            List<GeoPointVO> geoPointVOS = SectorCalculatorUtil.calculateSector(deviceModel);

            // 根据用户记忆勾选的标签列表，过滤可视域范围内的数据
            long time5 = new Date().getTime();
            List<ArTagMoveRes> arTagMoveList = this.filterArTagMoveList(user, arTagMoveReq, arTagInfoReqList, geoPointVOS);
            log.info(threadId + "-arTagMoveList.tagIdList" + JSON.toJSONString(arTagMoveList.stream().map(ArTagMoveRes::getTagId).collect(Collectors.toList())));
            log.info(threadId + "-filterArTagMoveList耗时" + (new Date().getTime() - time5) +"ms");

            // 根据经纬度转换为ptz
            List<ArTagMoveRes> arTagMovePtzList = this.getPtzByLonLatForArTag(arTagMoveList, deviceModel, ptzPosition);

            // 根据ptz转换百分比坐标
            List<ArTagMoveRes> percentByPtzList = this.getPercentByPtzForArTag(arTagMovePtzList, deviceModel);

            //按照上一版本格式返回数据，方便前端处理
            long time8 = new Date().getTime();
            result = this.beautifyStructure(percentByPtzList);
            log.info(threadId + "-beautifyStructure耗时" + (new Date().getTime() - time8) +"ms");
        }
        return result;
    }


    private List<ArDeviceInfo> getArTagMoveListOld(ArTagMoveReq arTagMoveReq, String threadId){
        long time = new Date().getTime();
        ArTagsInfoReq arTagsInfoReq = this.getArTagsInfoReq(arTagMoveReq);
        List<ArDeviceInfo> arDeviceInfos = arTagsInfoService.queryAll(arTagsInfoReq);
        arDeviceInfos.forEach(e-> {
            e.setTagId(e.getId());
            // 获取标签坐标
            String[] coordinates = e.getCoordinate().split(",");
            if (coordinates.length == 2) {
                // 获取分辨率
                Integer resolutionX = e.getResolutionx();
                Integer resolutionY = e.getResolutiony();
                if (resolutionX == null || resolutionY == null || resolutionX == 0 || resolutionY == 0) {
                    // 分辨率不正确，跳过
                    log.info("标签：" + e.getTagId() + "分辨率不正确");
                } else {
                    // 根据分辨率和坐标计算坐标参数
                    e.setPercentU(String.valueOf(Double.parseDouble(coordinates[0]) / resolutionX));
                    e.setPercentV(String.valueOf(Double.parseDouble(coordinates[1]) / resolutionY));
                }
            }else{
                log.info("标签：" + e.getTagId() + "坐标不正确");
            }
        });
        List<ArDeviceInfo> result = new ArrayList<>(arDeviceInfos);
        log.info(threadId + "-getArTagMoveListOld耗时" + (new Date().getTime() - time) +"ms");
        return result;
    }

    private ArTagsInfoReq getArTagsInfoReq(ArTagMoveReq arTagMoveReq) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //按照之前的格式，处理视频手动标签
        ArTagsInfoReq arTagsInfoReq = new ArTagsInfoReq();
        arTagsInfoReq.setTenantId(user.getTenantId());
        arTagsInfoReq.setIndustryCode(user.getIndustryCode());
        arTagsInfoReq.setArViewId(arTagMoveReq.getDeviceCode());
        arTagsInfoReq.setChannelCode(arTagMoveReq.getChannelCode());
        arTagsInfoReq.setUserName(user.getUserName());
        arTagsInfoReq.setTagClass(VideoConstants.TAG_CLASS_101);
        return arTagsInfoReq;
    }

    private String setChannelCode(ArTagMoveReq arTagMoveReq){
        String channelCode = arTagMoveReq.getChannelCode();
        Set<String> deviceCodeList = new HashSet<>();
        deviceCodeList.add(arTagMoveReq.getDeviceCode());
        List<ArChannelInfo> arChannelInfo = arTagsInfoMapper.getArChannelInfo(deviceCodeList);

        for (ArChannelInfo channelInfo : arChannelInfo) {
            if (StringUtils.isNotBlank(channelInfo.getChannelName()) && channelInfo.getChannelName().endsWith("_0")) {
                channelCode = channelInfo.getChannelCode();
                log.info("替换后channelCode:"+channelInfo.getChannelCode());
                break;
            }
        }
        return channelCode;
    }

    private List<ArDeviceInfo> beautifyStructure(List<ArTagMoveRes> percentByPtzList){
        List<ArDeviceInfo> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(percentByPtzList)){
            Map<String, List<ArTagMoveRes>> tagMap = percentByPtzList.stream().collect(Collectors.groupingBy(ArTagMoveRes::getTagClass));
            for (Map.Entry<String, List<ArTagMoveRes>> sl : tagMap.entrySet()) {
                if(VideoConstants.TAG_CLASS_101.equals(sl.getKey()) || VideoConstants.TAG_CLASS_102.equals(sl.getKey())){
                    //按照之前的格式，处理视频手动标签
                    List<ArTagMoveRes> arTagMoveRes = tagMap.get(sl.getKey());
                    if(!CollectionUtils.isEmpty(arTagMoveRes)){
                        ArTagsInfoReq arTagsInfoReq = new ArTagsInfoReq();
                        arTagsInfoReq.setUserName(SecurityUtils.getLoginUser().getUser().getUserName());
                        arTagsInfoReq.setTagIdList(arTagMoveRes.stream().map(ArTagMoveRes::getTagId).collect(Collectors.toList()));
                        List<ArDeviceInfo> arDeviceInfos = arTagsInfoService.queryAll(arTagsInfoReq);
                        arDeviceInfos.stream().map(item -> {
                            percentByPtzList.stream().filter(area-> Objects.equals(String.valueOf(item.getId()),area.getTagId())
                            ).forEach(area-> {
                                item.setTagId(area.getTagId());
                                item.setPercentU(area.getPercentU());
                                item.setPercentV(area.getPercentV());
                            });
                            return item;
                        }).collect(Collectors.toList());
                        result.addAll(arDeviceInfos);
                    }
                } else if(VideoConstants.TAG_CLASS_104.equals(sl.getKey())){
                    Map<String, List<ArTagMoveRes>> collect = sl.getValue().stream().collect(Collectors.groupingBy(ArTagMoveRes::getPolygonId));
                    for (Map.Entry<String, List<ArTagMoveRes>> stringListEntry : collect.entrySet()) {
                        List<ArTagMoveRes> value = stringListEntry.getValue();
                        ArTagMoveRes ar = value.get(0);
                        ArDeviceInfo arDeviceInfo = new ArDeviceInfo();
                        BeanUtils.copyProperties(ar, arDeviceInfo);
                        List<ArDeviceInfo> percentList = new ArrayList<>();
                        for (ArTagMoveRes arTagMoveRes : value) {
                            ArDeviceInfo in = new ArDeviceInfo();
                            in.setLongitude(String.valueOf(arTagMoveRes.getLongitude()));
                            in.setLatitude(String.valueOf(arTagMoveRes.getLatitude()));
                            in.setPercentU(arTagMoveRes.getPercentU());
                            in.setPercentV(arTagMoveRes.getPercentV());
                            percentList.add(in);
                        }
                        //计算中心点百分比坐标
                        DevicePointVo centerPercent = this.findCenterPercent(percentList);
                        if(ObjectUtil.isNotEmpty(centerPercent)){
                            arDeviceInfo.setPercentU(centerPercent.getPercentX());
                            arDeviceInfo.setPercentV(centerPercent.getPercentY());
                        }
                        arDeviceInfo.setPercentList(percentList);
                        result.add(arDeviceInfo);
                    }
                } else{
                    for (ArTagMoveRes tagMoveRes : sl.getValue()) {
                        ArDeviceInfo arDeviceInfo = new ArDeviceInfo();
                        BeanUtils.copyProperties(tagMoveRes, arDeviceInfo);
                        result.add(arDeviceInfo);
                    }
                }
            }
        }
        return result;
    }

    public DevicePointVo findCenterPercent(List<ArDeviceInfo> monitorPoints) {
        DevicePointVo devicePointVo = new DevicePointVo();
        if (monitorPoints == null || monitorPoints.isEmpty()) {
            System.out.println("The list is empty or null.");
            return devicePointVo;
        }

        Float maxPercentX = Float.valueOf(monitorPoints.get(0).getPercentU());
        Float minPercentX = Float.valueOf(monitorPoints.get(0).getPercentU());
        Float maxPercentY = Float.valueOf(monitorPoints.get(0).getPercentV());
        Float minPercentY = Float.valueOf(monitorPoints.get(0).getPercentV());

        for (ArDeviceInfo point : monitorPoints) {
            Float currentPercentX = Float.valueOf(point.getPercentU());
            Float currentPercentY = Float.valueOf(point.getPercentV());

            if (currentPercentX > maxPercentX) {
                maxPercentX = currentPercentX;
            }
            if (currentPercentX < minPercentX) {
                minPercentX = currentPercentX;
            }
            if (currentPercentY > maxPercentY) {
                maxPercentY = currentPercentY;
            }
            if (currentPercentY < minPercentY) {
                minPercentY = currentPercentY;
            }
        }
        devicePointVo.setPercentX(String.format("%.3f", (minPercentX + maxPercentX) / 2));
        devicePointVo.setPercentY(String.format("%.3f", (minPercentY + maxPercentY) / 2));
        return devicePointVo;
    }

    public Result<List<ArTagMoveRes>> getDeviceVisibleLonLat(ArTagMoveReq arTagMoveReq){
        List<ArTagMoveRes> result = new ArrayList<>();
        //入参标签
        List<ArTagMoveVO> arTagInfoReqList = arTagMoveReq.getArTagInfoReqList();
        if(CollectionUtils.isEmpty(arTagInfoReqList)){
            //查询用户记忆的标签
            SysUser user = SecurityUtils.getLoginUser().getUser();
            arTagInfoReqList = this.getUserMemory(user, arTagMoveReq);
        }

        if(CollectionUtils.isNotEmpty(arTagInfoReqList)){
            // 获取主视频画面摄像机摄像机型号信息
            List<DeviceModelDTO> deviceModelList = videoPtzQueryDAO.getDeviceModelForArMove(arTagMoveReq.getDeviceCode());
            DeviceModelDTO deviceModel = deviceModelList.get(Constants.ZERO_NUM);
            if (StringUtils.isBlank(deviceModel.getMinFocus())
                    || StringUtils.isBlank(deviceModel.getPicSensorLength())
                    ||StringUtils.isBlank(deviceModel.getPicSensorWidth())
                    ||StringUtils.isBlank(deviceModel.getHeight())
            ) {
                throw new CustomException("摄像机型号信息错误！");
            }

            //处理通道字段，ar设备取球机通道，否则计算可视域会报错
//            String channelCode = this.setChannelCode(arTagMoveReq);

            // 查询主视频画面摄像机通道的PTZ
            ArTagMovePtzVO ptzPosition = this.queryPtzPosition(arTagMoveReq, deviceModel);

            // 查询主视频画面摄像机通道的可视域
            this.getDeviceVisibleByOne(deviceModel, ptzPosition);

            // 计算可视域扇形区域的经纬度坐标集合
            List<GeoPointVO> geoPointVOS = SectorCalculatorUtil.calculateSector(deviceModel);
            return Result.success(this.buildGeoTest(geoPointVOS));
        }

        return Result.data(result);
    }

    private List<ArTagMoveVO> getUserMemory(SysUser user, ArTagMoveReq arTagMoveReq){
        List<ArTagMoveVO> result = new ArrayList<>();
        String[] tagClass = new String[]{VideoConstants.TAG_CLASS_101,VideoConstants.TAG_CLASS_102,VideoConstants.TAG_CLASS_103,VideoConstants.TAG_CLASS_105,VideoConstants.TAG_CLASS_106,VideoConstants.TAG_CLASS_107,VideoConstants.TAG_CLASS_108};
        for (String aClass : tagClass) {
            String valueByKey = this.getValueByKey(aClass, user, arTagMoveReq);
            if (!StringUtils.isEmpty(valueByKey)) {
                ArTagMoveVO arTagMoveVO = new ArTagMoveVO();
                arTagMoveVO.setTagClass(aClass);
                arTagMoveVO.setTagIdList(Arrays.asList(valueByKey.split(Constants.COMMA)));
                result.add(arTagMoveVO);
            }
        }
        return result;
    }

    private String getMemoryKey(String memoryType,SysUser user,ArTagMoveReq arTagMoveReq){
        return com.chinatower.video.modules.memory.Constants.REDIS_MEMORY
                + user.getIndustryCode()
                + "labelTree_video_" + memoryType + "_" + arTagMoveReq.getChannelCode()
                + com.chinatower.video.modules.memory.Constants.REDIS_TYPE
                + user.getUserId();
    }

    private String getValueByKey(String tagClass,SysUser user, ArTagMoveReq arTagMoveReq){
        return RedisUtil.get(this.getMemoryKey(tagClass, user, arTagMoveReq), RedisDBType.PRIMARY);
    }


    public ArTagMovePtzVO queryPtzPosition(ArTagMoveReq arTagMoveReq, DeviceModelDTO deviceModel){
        ArTagMovePtzVO arTagMovePtzVO = new ArTagMovePtzVO();
        // 调用接口获取ptz
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.QUERY_PTZ_POSITION);
        apiInfoDTO.setDeviceCode(arTagMoveReq.getDeviceCode());
        apiInfoDTO.setChannelCode(arTagMoveReq.getChannelCode());
        Result<JSONObject> ptzResult = null;
        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PTZ_POSITION);
            // 接口路径参数
            path.setDeviceCode(arTagMoveReq.getDeviceCode());
            path.setChannelCode(arTagMoveReq.getChannelCode());
            paramter.setPath(path);
            // 属地化数据统一改造 add by wangyt 20240129
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(arTagMoveReq.getDeviceCode());
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            ptzResult = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.CAL_DEVICE_VISIBLE_ERROR +"===:{}", apiInfoDTO);
        }

        String transPan;
        String transTilt;
        String transZoom;
        if (ResultCode.SUCCESS.getCode() == ptzResult.getCode()) {
            // ptz 转换
            DeviceModelVO model = new DeviceModelVO();
            BeanUtils.copyProperties(deviceModel, model);
            JSONObject ptzData = ptzResult.getData().getJSONObject(DeviceNorthIntfConstants.DATA);
            transPan = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_PAN_STR), model, LongLatAngleUtils.TYPE_P);
            transTilt = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_TILT_STR), model, LongLatAngleUtils.TYPE_T);
            transZoom = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_ZOOM_STR), model, LongLatAngleUtils.TYPE_Z);
        } else {
            throw new CustomException(ptzResult.getMsg(), ptzResult.getCode());
        }
        arTagMovePtzVO.setTransPan(transPan);
        arTagMovePtzVO.setTransTilt(transTilt);
        arTagMovePtzVO.setTransZoom(transZoom);
        return arTagMovePtzVO;
    }

    private void getDeviceVisibleByOne(DeviceModelDTO deviceModel, ArTagMovePtzVO ptzPosition) {
        String transPan = ptzPosition.getTransPan();
        String transTilt = ptzPosition.getTransTilt();
        String transZoom = ptzPosition.getTransZoom();

        // 获取p0 t0
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(transPan);
        deviceModelVO = LongLatAngleUtils.getValidOffset(deviceModel.getRefPoints(), deviceModelVO, new ArrayList<>());
        deviceModel.setPan(deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0());
        deviceModel.setTilt(deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0());
        BigDecimal zoom = new BigDecimal(transZoom);
        // z为0判断，避免计算报错
        if (BigDecimal.ZERO.compareTo(zoom) == 0) {
            throw new CustomException(IndErrorMsgConstants.DEVICE_ZOOM_ZERO, HttpStatus.HTTP_INTERNAL_ERROR);
        }
        try {
            // f= f0*z'
            BigDecimal realFocal = new BigDecimal(deviceModel.getMinFocus()).multiply(zoom).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigDecimalTwo = new BigDecimal(2);
            BigDecimal doubleRealFocal = bigDecimalTwo.multiply(realFocal);
            // arctan(c/2f)
            double c2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorLength()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(c/2f)
            deviceModel.setHorizRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(c2f)).toPlainString());
            // arctan(d/2f)
            double d2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorWidth()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(d/2f)
            deviceModel.setVertiRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(d2f)).toPlainString());
            // p'-p0'
            BigDecimal pTemp = new BigDecimal(transPan).subtract(deviceModel.getPan());
            if(pTemp.compareTo(BigDecimal.ZERO) < 0){
                pTemp = pTemp.add(new BigDecimal(360));
            }
            deviceModel.setHorizViewRange(pTemp.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            // t'-t0'
            deviceModel.setVertiViewRange(new BigDecimal(transTilt).subtract(deviceModel.getTilt()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } catch (Exception e) {
            log.error("getDeviceVisibleByOne", e);
            throw new CustomException(IndErrorMsgConstants.DEVICE_CAL_VISIBLE_ERROR, HttpStatus.HTTP_INTERNAL_ERROR);
        }
    }

    private String buildGeo(List<GeoPointVO> geoPointVOS) {
        StringBuilder sb = new StringBuilder();
        if(CollectionUtils.isEmpty(geoPointVOS)){
            return null;
        }
        for (GeoPointVO geoPoint : geoPointVOS) {
            sb.append(geoPoint.getLongitude()).append(" ").append(geoPoint.getLatitude()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private String buildGeoTest(List<GeoPointVO> geoPointVOS) {
        StringBuilder sb = new StringBuilder();
        if(CollectionUtils.isEmpty(geoPointVOS)){
            return null;
        }
        sb.append("[");
        for (GeoPointVO geoPoint : geoPointVOS) {
            double[] doubles = VideoPositionUtil.wgs84togcj02(geoPoint.getLongitude(), geoPoint.getLatitude());
            geoPoint.setLongitude(doubles[0]);
            geoPoint.setLatitude(doubles[1]);
            sb.append("[").append(geoPoint.getLongitude()).append(",").append(geoPoint.getLatitude()).append("],");
        }
        log.info(sb.toString());
        String substring = sb.substring(0, sb.length() - 1);
        log.info(substring);
        substring = substring + "]";
        log.info(substring);
        return substring;
    }

    private List<ArTagMoveRes> filterArTagMoveList(SysUser user, ArTagMoveReq arTagMoveReq, List<ArTagMoveVO> arTagInfoReqList, List<GeoPointVO> geoPointVOS){
        List<ArTagMoveRes> list = new ArrayList<>();
        ArTagFilterVO arTagFilterVO = new ArTagFilterVO();
        arTagFilterVO.setGeoPointStr(this.buildGeo(geoPointVOS));
        arTagFilterVO.setArViewId(arTagMoveReq.getDeviceCode());
        arTagFilterVO.setChannelCode(arTagMoveReq.getChannelCode());
        arTagFilterVO.setTenantId(user.getTenantId());
        arTagFilterVO.setIndustryCode(user.getIndustryCode());

        list.addAll(this.getMoveList(arTagFilterVO, arTagInfoReqList));
        return list;
    }

    private List<ArTagMoveRes> getMoveList(ArTagFilterVO arTagFilterVO, List<ArTagMoveVO> arTagInfoReqList){
        List<ArTagMoveRes> list = new ArrayList<>();
        for (ArTagMoveVO arTagMoveVO : arTagInfoReqList) {
            //标签类别 101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
            String tagClass = arTagMoveVO.getTagClass();
            List<String> tagIdList = arTagMoveVO.getTagIdList();
            arTagFilterVO.setTagIdList(tagIdList);
            switch (tagClass)
            {
                case VideoConstants.TAG_CLASS_101:
                    list.addAll(videoPtzQueryDAO.getArTagByVideo(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_102:
                    list.addAll(videoPtzQueryDAO.getArTagByMap(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_103:
                    list.addAll(videoPtzQueryDAO.getArTagByDevice(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_104:
                    list.addAll(this.getArTagByLayer(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_105:
                    list.addAll(videoPtzQueryDAO.getArTagByIot(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_106:
                    list.addAll(videoPtzQueryDAO.getArTagByUav(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_107:
                    list.addAll(videoPtzQueryDAO.getArTagBySensor(arTagFilterVO));
                    break;
                case VideoConstants.TAG_CLASS_108:
                    list.addAll(videoPtzQueryDAO.getArTagByHorn(arTagFilterVO));
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    private List<ArTagMoveRes> getArTagByLayer(ArTagFilterVO arTagFilterVO){
        List<ArTagMoveRes> resultList =  new ArrayList<>();
        ArLayerTagReq arLayerTagReq = new ArLayerTagReq();
        BeanUtils.copyProperties(arTagFilterVO, arLayerTagReq);
        arLayerTagReq.setDeviceCode(arTagFilterVO.getArViewId());
        List<PolygonPatchConfig> polygonPatchConfigs = videoPtzQueryDAO.queryPolygonByLayerId(arLayerTagReq);
        for (PolygonPatchConfig polygonPatchConfig : polygonPatchConfigs) {
            String polygonPointsLonLat = polygonPatchConfig.getPolygonPointsLonLat();
            if(!StringUtils.isEmpty(polygonPointsLonLat)){
                List<ArDeviceInfo> ja = JSON.parseArray(polygonPointsLonLat,ArDeviceInfo.class);
                for (ArDeviceInfo arDeviceInfo : ja) {
                    ArTagMoveRes arTagMoveRes = new ArTagMoveRes();
                    BeanUtils.copyProperties(polygonPatchConfig, arTagMoveRes);
                    arTagMoveRes.setTagId(polygonPatchConfig.getLayerId());
                    arTagMoveRes.setTagsName(polygonPatchConfig.getLayerName());
                    arTagMoveRes.setTagClass(VideoConstants.TAG_CLASS_104);
                    arTagMoveRes.setLongitude(Double.parseDouble(arDeviceInfo.getLongitude()));
                    arTagMoveRes.setLatitude(Double.parseDouble(arDeviceInfo.getLatitude()));
                    resultList.add(arTagMoveRes);
                }
            }
        }
        return resultList;
    }

    public List<ArTagMoveRes> getPtzByLonLatForArTag(List<ArTagMoveRes> vertexList,
                                       DeviceModelDTO deviceModelIn, ArTagMovePtzVO ptzPosition) {
        List<ArTagMoveRes> vertexListNew = new ArrayList<>();
        // 根据顶点经纬度计算ptz
        if(CollectionUtils.isNotEmpty(vertexList)){
            DeviceModelVO deviceModel = new DeviceModelVO();
            BeanUtils.copyProperties(deviceModelIn, deviceModel);
            Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
            List<SysDictDataResponse> enumList = res.getData();
            List<SysDictData> enumListNew = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(enumList)) {
                enumList.forEach(item ->{
                    SysDictData sysDictData = new SysDictData();
                    BeanUtils.copyProperties(item,sysDictData);
                    enumListNew.add(sysDictData);
                });
            }
            List<DeviceRefPointVO> pointsValid = videoPtzQueryDAO.getDevicePointsValidByReal(deviceModel.getDeviceCode());
            for (ArTagMoveRes arTagMoveRes : vertexList) {
                try {
                    vertexListNew.add(this.getArTagMovePtz(deviceModel, pointsValid, arTagMoveRes, ptzPosition, enumListNew));
                } catch (Exception e) {
                    log.error("getPtzByLonLatForArTag for异常", e);
                }
            }
        }
        return vertexListNew;
    }

    private ArTagMoveRes getArTagMovePtz(DeviceModelVO deviceModel, List<DeviceRefPointVO> pointsValid,
                    ArTagMoveRes arTagMoveRes, ArTagMovePtzVO ptzPosition, List<SysDictData> enumListNew){
        ArTagMoveRes newArTagMoveRes = new ArTagMoveRes();
        this.computePtzByArTag(deviceModel, pointsValid, arTagMoveRes, Float.parseFloat(ptzPosition.getTransZoom()),enumListNew);
        BeanUtils.copyProperties(arTagMoveRes,newArTagMoveRes);

        PresetData centerPointPtz = new PresetData();
        centerPointPtz.setPan(Float.parseFloat(ptzPosition.getTransPan()));
        centerPointPtz.setTilt(Float.parseFloat(ptzPosition.getTransTilt()));
        centerPointPtz.setZoom(Float.parseFloat(ptzPosition.getTransZoom()));
        newArTagMoveRes.setCenterPointPtz(centerPointPtz);
        return newArTagMoveRes;
    }

    public void computePtzByArTag(DeviceModelVO deviceModel, List<DeviceRefPointVO> pointsValid,
                                          ArTagMoveRes arTagMoveRes, float zoom, List<SysDictData> enumListNew)
    {
        // 转动角度 P’值
        float pan = 0;
        // 转动角度 P’+P0值
        float pan0 = 0;
        BigDecimal p0;
        // 转动角度 tilt' 值
        float tilt = 0;
        // 转动角度 tilt' + tilt0值
        float tilt0 = 0;
        BigDecimal t0;
        // 把摄像机经纬度和打点经纬度封装到VideoPtzQueryReq对象中
        VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
        videoPtzQueryReq.setCameraLon(Double.parseDouble(deviceModel.getLongitude()));
        videoPtzQueryReq.setCameraLat(Double.parseDouble(deviceModel.getLatitude()));
        videoPtzQueryReq.setPointLon(arTagMoveRes.getLongitude());
        videoPtzQueryReq.setPointLat(arTagMoveRes.getLatitude());
        // 获取转动角度
        pan = this.getPan(videoPtzQueryReq);
        tilt = this.getTilt(videoPtzQueryReq,deviceModel);
        // 处理水平角偏差和俯仰角偏差 通过有效参考点进行计算  pan为换算后的P’值，horOffset、pitchOffset为换算后实际的P0和T0
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(String.valueOf(pan));
        deviceModelVO = LongLatAngleUtils.getValidOffsetByRealHorAngle(pointsValid, deviceModelVO,enumListNew);

        p0 = deviceModelVO.getP0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getP0();
        pan0 = BigDecimal.valueOf(pan).add(p0).floatValue();

        t0 = deviceModelVO.getT0() == null ? BigDecimal.valueOf(0) : deviceModelVO.getT0();
        tilt0 = BigDecimal.valueOf(tilt).add(t0).floatValue();

        //p（0-360）值超过360，取余
        if(pan0 >= 360){
            pan0 = pan0 % 360;
        }
        //p（0-360）值小于0，+360
        if(pan0 < 0){
            pan0 = 360 + pan0;
        }
        // 返回结果对象
        arTagMoveRes.setPan(pan0);
        arTagMoveRes.setTilt(tilt0);
        arTagMoveRes.setZoom(zoom);
        log.info("经纬度转换ptz参数，标签id:{},经度:{},纬度:{}，p:{},t:{},z:{}",
                arTagMoveRes.getTagId(),arTagMoveRes.getLongitude(),arTagMoveRes.getLatitude(),pan0,tilt0,zoom);
    }

    public List<ArTagMoveRes> getPercentByPtzForArTag(List<ArTagMoveRes> vertexPtzList, DeviceModelDTO deviceModel) {
        List<ArTagMoveRes> vertexPercentList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(vertexPtzList)){
            //中心点坐标
            PresetData centerPointPtz = vertexPtzList.get(Constants.ZERO_NUM).getCenterPointPtz();
            BigDecimal p0 = new BigDecimal(centerPointPtz.getPan());
            BigDecimal t0 = new BigDecimal(centerPointPtz.getTilt());
            float z0 = centerPointPtz.getZoom();

            if(!ObjectUtil.isEmpty(deviceModel)){
                double f0 = Double.valueOf(deviceModel.getMinFocus());
                double c = Double.valueOf(deviceModel.getPicSensorLength());
                double d = Double.valueOf(deviceModel.getPicSensorWidth());
                String vertiDownNeg = deviceModel.getVertiDownNeg();

                //水平视场角和垂直视场角计算
                double f = f0 * Double.valueOf(z0);
                BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
                BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));
                for (ArTagMoveRes arTagMoveRes : vertexPtzList) {
                    try {
                        vertexPercentList.add(this.getArTagMovePercent(arTagMoveRes, a, b, p0, t0, vertiDownNeg));
                    } catch (Exception e) {
                        log.error("getPercentByPtzForArTag for异常", e);
                    }
                }
            }
        }
        return vertexPercentList;
    }

    private ArTagMoveRes getArTagMovePercent(ArTagMoveRes arTagMoveRes, BigDecimal a, BigDecimal b, BigDecimal p0, BigDecimal t0, String vertiDownNeg){
        ArTagMoveRes newArTagMoveRes = new ArTagMoveRes();
        BeanUtils.copyProperties(arTagMoveRes, newArTagMoveRes);
        BigDecimal pan = new BigDecimal(arTagMoveRes.getPan());
        BigDecimal tilt = new BigDecimal(arTagMoveRes.getTilt());

        double panP0 = pan.subtract(p0).doubleValue();
        if(panP0 >= 360){
            panP0 = panP0 % 360;
        }
        //p（0-360）值小于0，+360
        if(panP0 < 0){
            panP0 = 360 + panP0;
        }
        double abs = 360 - panP0;
        double min = panP0;
        if(panP0 > abs){
            min = -abs;
        }

        BigDecimal subtract = t0.subtract(tilt);
        if(Constants.ONE.equals(vertiDownNeg)){
            subtract = subtract.negate();
        }

        //计算百分比坐标
        BigDecimal u = (BigDecimal.valueOf(min)).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
        BigDecimal v = (subtract).divide(b,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));

        newArTagMoveRes.setPercentU(String.valueOf(u));
        newArTagMoveRes.setPercentV(String.valueOf(v));
        return newArTagMoveRes;
    }

    @DataScope(tenantColumn = "dataTable.tenant_id",deviceColumn = "dataTable.device_code",industryCode = "dataTable.industry_code")
    public Result<GeojsonStruckRes> getPolygonPatchConfigList(ArLayerTagReq arLayerTagReq){
        //查出视频主画面摄像机通道，对应图层的图斑信息
        List<PolygonPatchConfig> polygonPatchConfigs = videoPtzQueryDAO.queryPolygonByLayerId(arLayerTagReq);
        if(!CollectionUtils.isEmpty(polygonPatchConfigs)){
            Set<String> tbbhs = polygonPatchConfigs.stream().map(PolygonPatchConfig::getFeatureId).collect(Collectors.toSet());
            GetXjxlFeaturesByIdsReq getXjxlFeaturesByIdsReq = new GetXjxlFeaturesByIdsReq();
            getXjxlFeaturesByIdsReq.setLayerID(arLayerTagReq.getLayerId());
            getXjxlFeaturesByIdsReq.setTbbhs(new ArrayList<>(tbbhs));
            getXjxlFeaturesByIdsReq.setReturnAllCols(true);
            getXjxlFeaturesByIdsReq.setReturnGeometry(true);
            return gisLayerAPI.getXjxlFeaturesByIds(getXjxlFeaturesByIdsReq);
        }
        return Result.data(null);
    }


    public static void main(String[] args) {
        //中心点坐标
        BigDecimal p0 = new BigDecimal("359.3");
        BigDecimal t0 = new BigDecimal("-12.3");
        float z0 = 1f;
//        BigDecimal p0 = new BigDecimal("12.7");
//        BigDecimal t0 = new BigDecimal("-12.3");
//        float z0 = 1f;

        double f0 = Double.valueOf("6.9");
        double c = Double.valueOf("7.2");
        double d = Double.valueOf("5.4");

//        double f0 = Double.valueOf("6.9");
//        double c = Double.valueOf("7.2");
//        double d = Double.valueOf("5.4");

        //水平视场角和垂直视场角计算
        double f = f0 * Double.valueOf(z0);
        BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
        BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));
        BigDecimal pan = new BigDecimal("11.609985");
        BigDecimal tilt = new BigDecimal("-22.28");
//        BigDecimal pan = new BigDecimal("355.63998");
//        BigDecimal tilt = new BigDecimal("-14.51");
        //计算百分比坐标

        double panP0 = pan.subtract(p0).doubleValue();
        if(panP0 >= 360){
            panP0 = panP0 % 360;
        }
        //p（0-360）值小于0，+360
        if(panP0 < 0){
            panP0 = 360 + panP0;
        }
        double abs = 360 - panP0;
        double min = panP0;
//        if(panP0 > abs){
//            min = -abs;
//        }

        BigDecimal subtract = t0.subtract(tilt);
        if(Constants.ONE.equals("0")){
            subtract = subtract.negate();
        }

        //计算百分比坐标
        BigDecimal u = (BigDecimal.valueOf(min)).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
        BigDecimal v = (subtract).divide(b,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
        log.info("u:"+u);
        log.info("v:"+v);

        //中心点坐标
//        BigDecimal p0 = new BigDecimal("12.7");
//        BigDecimal t0 = new BigDecimal("12.3");
//        float z0 = 1.0f;
//
//        double f0 = Double.valueOf("6.9");
//        double c = Double.valueOf("7.2");
//        double d = Double.valueOf("5.4");
//        //水平视场角和垂直视场角计算
//        double f = f0 * Double.valueOf(1);
//        BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f))));
//        BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f))));
//        BigDecimal pan = new BigDecimal("355.63998");
//        BigDecimal tilt = new BigDecimal("14.51");
////        BigDecimal pan = new BigDecimal("0");
////        BigDecimal tilt = new BigDecimal("12.3");
//
//        double ff = Math.abs((355.64 - 12.7));
//        double abs = Math.abs(360 - ff);
//        double min = Math.min(ff, abs);
//
//        //计算百分比坐标
//        BigDecimal u = (BigDecimal.valueOf(50)).add(BigDecimal.valueOf(min).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
////        BigDecimal u = (p0.add(BigDecimal.valueOf(360)).subtract(pan)).divide(a,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(0.5));
//        BigDecimal v = (BigDecimal.valueOf(50)).subtract((tilt.subtract(t0)).divide(b,Constants.FOUR_NUM, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
//        log.info("u:"+u);
//        log.info("v:"+v);
    }

}
