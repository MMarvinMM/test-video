package com.chinatower.airline.controller;

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
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 无人机云台控制控制器
 *
 * @author ljy
 * @date 2025/12/30
 */
@Slf4j
@RestController
@Api(value = "无人机飞行控制", tags = "无人机飞行控制")
@RequestMapping("/air-control")
public class AirControlController {
    
    @Resource
    private AirControlService airControlService;
    
    
    @ApiOperation("进入飞行控制模式")
    @PostMapping("enter")
    public Result<String> drcModeEnter(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用进入飞行控制模式接口:{}", req);
        Result<String> result  = airControlService.drcModeEnter(req);
        log.info("调用进入飞行控制模式结束");
        return result;
    }
    
    @ApiOperation("退出飞行控制")
    @PostMapping("exit")
    public Result<String> drcModeExit(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用退出飞行控制模式接口:{}", req);
        Result<String> result = airControlService.drcModeExit(req);
        log.info("调用退出飞行控制模式接口结束");
        return result;
    }
    
    @ApiOperation("变焦")
    @PostMapping("set/ratio")
    public Result<String> setRatio(@RequestBody @Validated ChargeRatioReq req){
        log.info("调用切换直播镜头控制接口:{}", req);
        Result<String> result = airControlService.setRatio(req);
        log.info("调用切换直播镜头控制接口结束");
        return result;
    }
    
    @ApiOperation("拍照")
    @PostMapping("takePhoto")
    public Result<String> takePhoto(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用拍照接口:{}", req);
        Result<String> result = airControlService.takePhoto(req);
        log.info("调用拍照接口结束");
        return result;
    }
    
    @ApiOperation("重置云台")
    @PostMapping("reset")
    public Result<String> reset(@RequestBody @Validated GimbalResetReqBase req){
        log.info("调用重置云台接口:{}", req);
        Result<String> result = airControlService.reset(req);
        log.info("调用重置云台接口结束");
        return result;
    }
    
    @ApiOperation("暂停航线")
    @PostMapping("pause/mission")
    public Result<String> pauseMission(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用暂停航线接口:{}", req);
        Result<String> result = airControlService.pauseMission(req);
        log.info("调用暂停航线模式结束");
        return result;
    }
    
    
    @ApiOperation("无人机获取/取消云台控制权")
    @PostMapping("ops/gimbal")
    public Result<OpsGimbalResp> opsGimbal(@RequestBody @Validated OpsGimbalReq req){
        log.info("调用无人机获取/取消云台控制权接口:{}", req);
        Result<OpsGimbalResp> result = airControlService.opsGimbal(req);
        log.info("调用无人机获取/取消云台控制权结束");
        return result;
    }
    
    @ApiOperation("开启全景拍照")
    @PostMapping("cameraPhotoTake")
    public Result<String> cameraPhotoTake(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用开启全景拍照接口:{}", req);
        Result<String> result = airControlService.cameraPhotoTake(req);
        log.info("调用开启全景拍照结束");
        return result;
    }
    
    @ApiOperation("结束全景拍照")
    @PostMapping("cameraPhotoStop")
    public Result<String> cameraPhotoStop(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用结束全景拍照接口:{}", req);
        Result<String> result = airControlService.cameraPhotoStop(req);
        log.info("调用结束全景拍照结束");
        return result;
    }
    
    @ApiOperation("一键返航")
    @PostMapping("go/home")
    public Result<String> returnHome(@RequestBody @Validated ReturnHomeReq req){
        log.info("调用一键返航接口:{}", req);
        Result<String> result = airControlService.returnHome(req);
        log.info("调用一键返航结束");
        return result;
    }
    
    @ApiOperation("取消返航")
    @PostMapping("returnHomeCancel")
    public Result<String> returnHomeCancel(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用取消返航接口:{}", req);
        Result<String> result = airControlService.returnHomeCancel(req);
        log.info("调用取消返航结束");
        return result;
    }

    @ApiOperation("录像控制")
    @PostMapping("recording")
    public Result<String> recording(@RequestBody @Validated RecordingReq req){
        log.info("调用录像控制接口:{}", req);
        Result<String> result = airControlService.recording(req);
        log.info("调用录像控制结束");
        return result;
    }
    
    @ApiOperation("继续航线")
    @PostMapping("continue/mission")
    public Result<String> continueMission(@RequestBody @Validated ContinueMissionReq req){
        log.info("调用继续航线接口:{}", req);
        Result<String> result = airControlService.continueMission(req);
        log.info("调用继续航线结束");
        return result;
    }
    
    @ApiOperation("飞行控制")
    @PostMapping("droneControl")
    public Result<String> droneControl(@RequestBody @Validated DroneControlReq req){
        log.info("调用飞行控制接口:{}", req);
        Result<String> result = airControlService.droneControl(req);
        log.info("调用飞行控制结束");
        return result;
    }
    
    @ApiOperation("拖拽修改云台朝向")
    @PostMapping("cameraScreenDrag")
    public Result<String> cameraScreenDrag(@RequestBody @Validated CameraScreenDragReq req){
        log.info("拖拽修改云台朝向:{}", req);
        Result<String> result = airControlService.cameraScreenDrag(req);
        log.info("拖拽修改云台朝向");
        return result;
    }


    @ApiOperation("镜头转向")
    @PostMapping("control/gimbal")
    public Result<String> controlGimbal(@RequestBody @Validated ControlGimbalReq req){
        log.info("调用镜头转向接口:{}", req);
        Result<String> result = airControlService.controlGimbal(req);
        log.info("调用镜头转向结束");
        return result;
    }

    @ApiOperation("急停")
    @PostMapping("stop/process")
    public Result<String> emergencyStop(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("调用急停接口:{}", req);
        Result<String> result = airControlService.emergencyStop(req);
        log.info("调用急停接口结束");
        return result;
    }

    @ApiOperation("切换镜头")
    @PostMapping("camera/zoom")
    public Result<String> cameraZoom(@RequestBody @Validated CameraZoomReq req){
        log.info("调用切换镜头接口:{}", req);
        Result<String> result = airControlService.cameraZoom(req);
        log.info("调用切换镜头接口结束");
        return result;
    }

    @ApiOperation("查询镜头参数")
    @PostMapping("camera/lens/acquire")
    public Result acquire(@RequestBody @Validated BaseAirDeviceWithNodeInfoReq req){
        log.info("查询镜头参数:{}", req);
        Result result = airControlService.acquire(req);
        log.info("查询镜头参数结束");
        return result;
    }

}
