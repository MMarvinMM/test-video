package com.chinatower.airline.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.request.BaseAirDeviceReq;
import com.chinatower.airline.dto.request.BaseAirDeviceWithNodeInfoReq;
import com.chinatower.airline.dto.request.CameraScreenDragReq;
import com.chinatower.airline.dto.request.CameraZoomReq;
import com.chinatower.airline.dto.request.ChargeRatioReq;
import com.chinatower.airline.dto.request.ContinueMissionReq;
import com.chinatower.airline.dto.request.ControlGimbalReq;
import com.chinatower.airline.dto.request.DroneControlReq;
import com.chinatower.airline.dto.request.GimbalResetReqBase;
import com.chinatower.airline.dto.request.OpsGimbalReq;
import com.chinatower.airline.dto.request.RecordingReq;
import com.chinatower.airline.dto.request.ReturnHomeReq;
import com.chinatower.airline.dto.response.OpsGimbalResp;
import com.chinatower.airline.service.AirControlService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 云台控制服务实现
 *
 * @author ljy
 * @date 2025/12/30
 */
@Service
@Slf4j
public class AirControlServiceImpl implements AirControlService {
    
    @Resource
    private UavOkhttpUtil uavOkhttpUtil;
    @Resource
    private NetConnectUtils netConnectUtils;
    
    @Override
    public Result<String> drcModeEnter(BaseAirDeviceWithNodeInfoReq req) {
        //获取AcceptNode 受理节点
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.DRC_MODE_ENTER, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> drcModeExit(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.DRC_MODE_EXIT, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> setRatio(ChargeRatioReq req) {
        req.setAcceptNode(getAcceptNode());
        postWithToken(req, NetConnectUtils.RequestParamter.URLMappingEnum.SET_RATIO, req.getXToken());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    
    @Override
    public Result<String> takePhoto(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.TAKE_PHOTO, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> reset(GimbalResetReqBase req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.GIMBAL_RESET, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    
    @Override
    public Result<String> pauseMission(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.PAUSE_MISSION, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<OpsGimbalResp> opsGimbal(OpsGimbalReq req) {
        req.setAcceptNode(getAcceptNode());
        ResultEntity resultEntity = post(req, NetConnectUtils.RequestParamter.URLMappingEnum.OPS_GIMBAL,
                req.getAccessNode(), req.getAcceptNode());
        Object data = resultEntity.getData();
        if(Objects.nonNull(data)){
            return Result.data(JSON.parseObject(data.toString(), OpsGimbalResp.class));
        }else{
            log.info("调用opsGimbalResp没有返回值");
            return Result.data(new OpsGimbalResp());
        }
    }
    
    @Override
    public Result<String> cameraPhotoTake(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.CAMERA_PHOTO_TAKE, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> cameraPhotoStop(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.CAMERA_PHOTO_STOP, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> returnHome(ReturnHomeReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.RETURN_HOME, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result<String> returnHomeCancel(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.RETURN_HOME_CANCEL, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result<String> recording(RecordingReq req) {
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.RECORDING, null, null);
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> continueMission(ContinueMissionReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.CONTINUE_MISSION, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> droneControl(DroneControlReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.DRONE_CONTROL, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }
    
    @Override
    public Result<String> cameraScreenDrag(CameraScreenDragReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.CAMERA_SCREEN_DRAG, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result<String> controlGimbal(ControlGimbalReq req) {
        req.setAcceptNode(getAcceptNode());
        postWithToken(req, NetConnectUtils.RequestParamter.URLMappingEnum.CONTROL_GIMBAL, req.getXToken());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result<String> emergencyStop(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        post(req, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_PROCESS, req.getAccessNode(), req.getAcceptNode());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result<String> cameraZoom(CameraZoomReq req) {
        req.setAcceptNode(getAcceptNode());
        postWithToken(req, NetConnectUtils.RequestParamter.URLMappingEnum.CAMERA_ZOOM, req.getXToken());
        return Result.success(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public Result acquire(BaseAirDeviceWithNodeInfoReq req) {
        req.setAcceptNode(getAcceptNode());
        ResultEntity result = post(req, NetConnectUtils.RequestParamter.URLMappingEnum.ACQUIRE, req.getAccessNode(), req.getAcceptNode());
        return Result.data(result.getData());
    }


    /**
     * 获取受理节点
     *
     * @return {@link String}
     */
    private String getAcceptNode() {
        return uavOkhttpUtil.getSelfPlatFormCode();
    }
    
    /**
     * 通用HTTP POST请求方法（复用重复逻辑）
     * @param body 请求体对象
     * @param url 请求URL
     * @param platformCode 平台编码
     * @param accessNode 接入节点
     * @return 响应结果
     */
    private static final String OK = "0000";
    private <T> ResultEntity post(T body, NetConnectUtils.RequestParamter.URLMappingEnum url, String platformCode, String accessNode) {
        //构建发送参数
        NetConnectUtils.RequestParamter<T> params = new NetConnectUtils.RequestParamter<>();
        params.setBody(body);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        if(url == NetConnectUtils.RequestParamter.URLMappingEnum.PAUSE_MISSION
                || url == NetConnectUtils.RequestParamter.URLMappingEnum.DRONE_CONTROL
                || url == NetConnectUtils.RequestParamter.URLMappingEnum.STOP_PROCESS){
            //路径参数传参
            path.setDeviceCode(((BaseAirDeviceReq)body).getDeviceCode());
        }
        path.setUrl(url);
        params.setPath(path);
        
        ResultEntity cr = null;
        try {
            log.info("开始调用新大陆网关:接入节点{}接收节点{}", accessNode, platformCode);
            cr = netConnectUtils.post(params, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(accessNode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("uav"),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(params)));
            log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关", e);
        }
        if (ObjectUtil.isNotNull(cr) && !OK.equals(cr.getReturnCode())) {
            throw new CustomException("调用飞控平台返回错误:" + cr.getErrorMsg());
        }
        // 统一空值校验：如果返回null，直接判定为失败
        if (cr == null) {
            throw new CustomException("调用网关接失败");
        }
        return cr;
    }
    
    /**
     * 携带令牌的通用HTTP POST请求方法（复用重复逻辑）
     * @param req 请求参数
     * @param urlMapping 请求URL
     * @param xToken 获取云台控制权后飞控平台返回的令牌
     *
     * @return 响应结果
     */
    private <T extends BaseAirDeviceWithNodeInfoReq> ResultEntity postWithToken(T req, NetConnectUtils.RequestParamter.URLMappingEnum urlMapping, String xToken) {
        //构建发送参数
        NetConnectUtils.RequestParamter<T> param = new NetConnectUtils.RequestParamter<>();
        param.setBody(req);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMapping);
        //部分令牌接口（如切换镜头）路径中携带deviceCode，统一填充，模板未使用时忽略
        path.setDeviceCode(req.getDeviceCode());
        param.setPath(path);

        ResultEntity cr = null;
        try {
            log.info("开始调用新大陆网关:接入节点{}接收节点{}", req.getAccessNode(), req.getAcceptNode());
            cr = netConnectUtils.post(param, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(req.getAcceptNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(req.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("uav"),
                    NetConnectUtils.HeaderKeyEnum.X_Token.of(xToken),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(param)));
            log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关", e);
        }
        if (ObjectUtil.isNotNull(cr) && !OK.equals(cr.getReturnCode())) {
            throw new com.chinatower.platform.common.exception.CustomException(
                    "调用飞控平台返回错误:" + cr.getErrorMsg());
        }
        log.info("通用请求入参：{},出参:{}",param, JSON.toJSONString(cr));
        // 统一空值校验：如果返回null，直接判定为失败
        if (cr == null) {
            throw new CustomException("调用网关失败");
        }
        return cr;
    }
}
