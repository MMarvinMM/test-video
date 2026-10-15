package com.chinatower.airline.service;

import com.chinatower.airline.dto.request.AirVideoReq;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;

/**
 * @author WLF
 * @description
 * @date 2025/1/14 15:59
 */
public interface AirMedinumService {
    /**
     * @param airVideoReq
     * @return
     */
    Result getVideoRealtimeUrl(AirVideoReq airVideoReq);

    /**
     *
     * @param airVideoReq
     * @return
     */
    Result switchLiveStream(AirVideoReq airVideoReq);

    /**
     * 获取机巢视频流
     * @param airVideoReq
     * @return
     */
    Result getUavNestStream(AirVideoReq airVideoReq);

    /**
     * 实时喊话
     * @param airHornReq
     * @return
     */
    void liveHorn(AirHornReq airHornReq);

    /**
     * 停止播放
     * @param playStopDto
     * @return
     */
    ResultEntity stopPlay(PlayStopDto playStopDto);
    /**
     * 设置播放音量
     * @param playVolumeSetDto
     * @return
     */
    ResultEntity setPlayVolume(PlayVolumeSetDto playVolumeSetDto);
    /**
     * 文本播放
     * @param dto
     * @return
     */
    ResultEntity setPlayTts(PlayTtsSetDto dto);
    /**
     * 文件播放
     * @param playModeSetDto
     * @return
     */
    ResultEntity setPlay(PlayModeSetDto playModeSetDto);

    /**
     * 开启关闭探照灯
     * @param searchlightSwitchDto
     * @return
     */
    ResultEntity searchlightSwitch(SearchlightSwitchDto searchlightSwitchDto);

    /**
     * 探照灯调节亮度
     * @param searchlightDto
     * @return
     */
    ResultEntity searchlight(SearchlightDto searchlightDto);

    /**
     * 无人机航点通知喇叭调整
     * @param airTaskStatusProcessDto
     */
    void taskStatusProcess(AirTaskStatusProcessDto airTaskStatusProcessDto);

    /**
     * 查询探照灯状态
     * @param dto
     * @return
     */
    String querySearchlightStatus(AirSearchlightStatusDto dto);

    /**
     * 设置探照灯状态
     * @param dto
     * @return
     */
    void setSearchlightStatus(AirSearchlightStatusDto dto);


    Result calibration(AirSearchlightStatusDto dto);
}
