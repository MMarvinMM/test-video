package com.chinatower.airline.service;

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
import com.chinatower.common.entity.Result;

/**
 * 云台控制服务
 *
 * @author ljy
 * @date 2025/12/30
 */
public interface AirControlService {
    
    /**
     * 进入云台控制
     *
     * @param req 请求参数
     * @return 结果
     */
    Result<String> drcModeEnter(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 退出云台控制
     *
     * @param req 退出云台控制请求参数
     * @return 结果
     */
    Result<String> drcModeExit(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 变焦切换
     *
     * @param req 变焦切换参数
     * @return 变焦切换结果
     */
    Result<String> setRatio(ChargeRatioReq req);
    
    
    /**
     * 拍照
     *
     * @param req 拍照请求参数
     * @return 拍照结果
     */
    Result<String> takePhoto(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 重置云台
     *
     * @param req 重置云台请求参数
     * @return 重置云台结果
     */
    Result<String> reset(GimbalResetReqBase req);
    
    
    /**
     * 暂停航线
     *
     * @param req 暂停航线请求参数
     * @return 暂停航线结果
     */
    Result<String> pauseMission(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 云台控制
     *
     * @param req 云台控制请求参数
     * @return 云台控制结果
     */
    Result<OpsGimbalResp> opsGimbal(OpsGimbalReq req);
    
    /**
     * 开启全景拍照
     *
     * @param req 拍照请求参数
     * @return 拍照结果
     */
    Result<String> cameraPhotoTake(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 停止全景拍照
     *
     * @param req 拍照请求参数
     * @return 拍照结果
     */
    Result<String> cameraPhotoStop(BaseAirDeviceWithNodeInfoReq req);
    
    /**
     * 一键返航
     *
     * @param req 返航请求参数
     * @return 返航结果
     */
    Result<String> returnHome(ReturnHomeReq req);

    /**
     * 取消返航
     *
     * @param req 取消返航请求参数
     * @return 取消返航结果
     */
    Result<String> returnHomeCancel(BaseAirDeviceWithNodeInfoReq req);

    /**
     * 录像
     *
     * @param req 录像请求参数
     * @return 录像结果
     */
    Result<String> recording(RecordingReq req);
    
    /**
     * 继续航线
     *
     * @param req 继续航线请求参数
     * @return 继续航线结果
     */
    Result<String> continueMission(ContinueMissionReq req);
    
    /**
     * 无人机控制
     *
     * @param req 无人机控制请求参数
     * @return 无人机控制结果
     */
    Result<String> droneControl(DroneControlReq req);
    
    /**
     * 云台屏幕拖拽
     *
     * @param req 屏幕拖拽请求参数
     * @return 屏幕拖拽结果
     */
    Result<String> cameraScreenDrag(CameraScreenDragReq req);

    /**
     * 镜头转向
     *
     * @param req 镜头转向请求参数
     * @return 镜头转向结果
     */
    Result<String> controlGimbal(ControlGimbalReq req);

    /**
     * 急停（无人机停止所有动作并悬停）
     *
     * @param req 急停请求参数
     * @return 急停结果
     */
    Result<String> emergencyStop(BaseAirDeviceWithNodeInfoReq req);

    /**
     * 切换镜头
     *
     * @param req 切换镜头请求参数
     * @return 切换镜头结果
     */
    Result<String> cameraZoom(CameraZoomReq req);

    Result acquire(BaseAirDeviceWithNodeInfoReq req);
}
