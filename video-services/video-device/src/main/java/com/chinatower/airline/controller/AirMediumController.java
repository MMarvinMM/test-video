package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.dto.request.AirVideoReq;
import com.chinatower.airline.service.AirMedinumService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.Constants;
import com.chinatower.device.entity.dto.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author WLF
 * @description
 * @date 2025/1/14 15:56
 */
@RestController
@Api(value = "无人机媒体文件", tags = "无人机媒体文件")
@Slf4j
@RequestMapping("/airMedium")
public class AirMediumController {
    @Resource
    private AirMedinumService airMedinumService;
    @Resource
    private UavOkhttpUtil uavOkhttpUtil;

    /**
     * 获取视频实时地址
     *
     * @param
     * @return
     */
    @PostMapping("/getVideoRealtimeUrl")
    public Result getVideoRealtimeUrl(@RequestBody AirVideoReq airVideoReq) {
        log.info("AirLineController-getVideoRealtimeUrl-req:{}", JSON.toJSONString(airVideoReq));
        // 获取平台节点
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airVideoReq.setAcceptNode(platformCode);
        return airMedinumService.getVideoRealtimeUrl(airVideoReq);
    }

    /**
     * 切换直播流清晰度
     */
    @PostMapping("/switchLiveStream")
    public Result switchLiveStream(@RequestBody AirVideoReq airVideoReq) {
        log.info("AirLineController-switchLiveStream-req:{}", JSON.toJSONString(airVideoReq));
        return airMedinumService.switchLiveStream(airVideoReq);
    }

    /**
     * 获取机巢直播流地址（机巢拉流）
     * @param airVideoReq
     * @return
     */
    @PostMapping("/getUavNestStream")
    public Result getUavNestStream(@RequestBody AirVideoReq airVideoReq) {
        log.info("AirLineController-getUavNestStream-req:{}", JSON.toJSONString(airVideoReq));
        // 获取平台节点
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airVideoReq.setAcceptNode(platformCode);
        return airMedinumService.getUavNestStream(airVideoReq);
    }

    /**
     * 实时喊话
     * @param airHornReq
     * @return
     */
    @PostMapping("/liveHorn")
    public Result liveHorn(@RequestBody AirHornReq airHornReq) {
        log.info("AirMediumController-liveHorn-req:{}", JSON.toJSONString(airHornReq));
        try {
            airMedinumService.liveHorn(airHornReq);
            return Result.success("");
        }catch (Exception e) {
            log.error("实时喊话失败:",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 喇叭停止播放
     * @param playStopDto
     * @return Result 统一响应格式
     */
    @PostMapping("/stopPlay")
    public Result stopPlay(@RequestBody PlayStopDto playStopDto) {
        log.info("AirMediumController-stopPlay-req:{}", JSON.toJSONString(playStopDto));
        return convertToResult(airMedinumService.stopPlay(playStopDto));
    }

    /**
     * 设置播放音量
     * @param playVolumeSetDto
     * @return Result 统一响应格式
     */
    @PostMapping("/setPlayVolume")
    public Result setPlayVolume(@RequestBody PlayVolumeSetDto playVolumeSetDto) {
        log.info("AirMediumController-setPlayVolume-req:{}", JSON.toJSONString(playVolumeSetDto));
        return convertToResult(airMedinumService.setPlayVolume(playVolumeSetDto));
    }

    /**
     * 文本播放
     * @param dto
     * @return Result 统一响应格式
     */
    @PostMapping("/setPlayTts")
    public Result setPlayTts(@RequestBody PlayTtsSetDto dto) {
        log.info("AirMediumController-setPlayTts-req:{}", JSON.toJSONString(dto));
        return convertToResult(airMedinumService.setPlayTts(dto));
    }

    /**
     * 文件播放
     * @param dto
     * @return Result 统一响应格式
     */
    @PostMapping("/setPlay")
    public Result setPlay(@RequestBody PlayModeSetDto dto) {
        log.info("AirMediumController-setPlay-req:{}", JSON.toJSONString(dto));
        return convertToResult(airMedinumService.setPlay(dto));
    }

    /**
     * 开启关闭探照灯
     * @param dto
     * @return Result 统一响应格式
     */
    @PostMapping("/searchlightSwitch")
    public Result searchlightSwitch(@RequestBody SearchlightSwitchDto dto) {
        log.info("AirMediumController-searchlightSwitch-req:{}", JSON.toJSONString(dto));
        return convertToResult(airMedinumService.searchlightSwitch(dto));
    }

    /**
     * 探照灯调节亮度
     * @param dto
     * @return Result 统一响应格式
     */
    @PostMapping("/searchlight")
    public Result searchlight(@RequestBody SearchlightDto dto) {
        log.info("AirMediumController-searchlight-req:{}", JSON.toJSONString(dto));
        return convertToResult(airMedinumService.searchlight(dto));
    }

    /**
     * 将网关返回的 ResultEntity 转换为前端统一的 Result 格式
     * @param resultEntity 网关/内部接口返回的 ResultEntity
     * @return com.chinatower.common.entity.Result
     */
    private Result convertToResult(ResultEntity resultEntity) {
        if (resultEntity == null) {
            return Result.fail("请求失败");
        }
        if (Constants.SUCCESS_CODE.equals(resultEntity.getReturnCode())) {
            Object data = resultEntity.getData();
            return data != null ? Result.data(data) : Result.success("");
        }
        String errorMsg = resultEntity.getErrorMsg() != null ? resultEntity.getErrorMsg() : "操作失败";
        return Result.fail(errorMsg);
    }

    /**
     * 无人机航点通知喇叭调整
     * @param airTaskStatusProcessDto
     * @return
     */
    @PostMapping("/taskStatusProcess")
    public Result taskStatusProcess(@RequestBody AirTaskStatusProcessDto airTaskStatusProcessDto) {
        log.info("AirMediumController-taskStatusProcess-req:{}", JSON.toJSONString(airTaskStatusProcessDto));
        airMedinumService.taskStatusProcess(airTaskStatusProcessDto);
        return Result.success("");
    }

    /**
     * 查询探照灯状态
     * @param dto
     * @return
     */
    @PostMapping("/querySearchlightStatus")
    public Result querySearchlightStatus(@RequestBody AirSearchlightStatusDto dto) {
        log.info("AirMediumController-querySearchlightStatus-req:{}", JSON.toJSONString(dto));
        return Result.success(airMedinumService.querySearchlightStatus(dto));
    }

    /**
     * 设置探照灯状态
     * @param dto
     * @return
     */
    @PostMapping("/setSearchlightStatus")
    public Result setSearchlightStatus(@RequestBody AirSearchlightStatusDto dto) {
        log.info("AirMediumController-setSearchlightStatus-req:{}", JSON.toJSONString(dto));
        try {
            airMedinumService.setSearchlightStatus(dto);
            return Result.success("");
        }catch (Exception e) {
            return Result.fail("设置探照灯状态失败:"+e.getMessage());
        }

    }

    /**
     * 手动下发标定
     */
    @PostMapping("/calibration")
    public Result calibration(@RequestBody AirSearchlightStatusDto dto) {
        log.info("AirMediumController-calibration-req:{}", JSON.toJSONString(dto));
        try {
            return airMedinumService.calibration(dto);
        }catch (Exception e) {
            return Result.fail("手动下发标定:"+e.getMessage());
        }

    }
}
