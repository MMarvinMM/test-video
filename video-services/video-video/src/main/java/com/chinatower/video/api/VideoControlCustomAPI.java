package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @ClassName: VideoControlCustomAPI
 * @Description: 视频动作控制相关接口
 * @Author: zhujun
 * @Date: 2022/1/11
 **/
public interface VideoControlCustomAPI {

    /**
     * 启用/停用巡航路径
     * 先获取控制权
     *
     * @param doPatrolReq
     * @return
     */
    @PostMapping("/video/control/doPatrol")
    Result doPatrol(@RequestBody DoPatrolReq doPatrolReq);

    /**
     * 开始/结束云台转动
     * 先获取控制权
     *
     * @param doPtzCmdsReq
     * @return
     */
    @PostMapping("/video/control/doPtzCmds")
    Result doPtzCmds(@RequestBody DoPtzCmdsReq doPtzCmdsReq);

    /**
     * 开始/结束聚焦(focus)/缩放(zoom)
     *
     * @param doCameraFocusOrZoomReq
     * @return
     */
    @PostMapping("/video/control/doCameraFocusOrZoom")
    Result doCameraFocusOrZoom(@RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq);

    /**
     * 转到指定PTZ位置
     *
     * @param doPtzLocationCmdReq
     * @return
     */
    @PostMapping("/video/control/doPtzLocationCmd")
    Result doPtzLocationCmd(@RequestBody DoPtzLocationCmdReq doPtzLocationCmdReq);

    /**
     * 转到预置点
     *
     * @param doControlPresetReq
     * @return
     */
    @PostMapping("/video/control/doControlPreset")
    Result doControlPreset(@RequestBody DoControlPresetReq doControlPresetReq);

    /**
     * 获取云台控制权
     *
     * @param videoBaseControlReq
     * @return
     */
    @PostMapping("/video/control/doLock")
    Result<DoLockOrReleasePtzData> doLock(@RequestBody VideoBaseControlReq videoBaseControlReq);

    /**
     * 释放云台控制权
     *
     * @param videoBaseControlReq
     * @return
     */
    @PostMapping("/video/control/doRelease")
    Result doRelease(@RequestBody VideoBaseControlReq videoBaseControlReq);

    /**
     * 开启/关闭灯
     * @param playerDoSwitchLightReq
     * @return
     */
    @PostMapping("/video/control/doSwitchLight")
    Result doSwitchLight(@RequestBody PlayerDoSwitchLightReq playerDoSwitchLightReq);

    /**
     * 设置通道参数
     *
     * @param doSetChannelParamReq
     * @return
     */
    @PostMapping("/video/control/doSetChannelParam")
    Result doSetChannelParam(@RequestBody DoSetChannelParamReq doSetChannelParamReq);

    /**
     * 功能描述: 查询云台锁信息接口
     * @param: videoBaseControlReq
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/11/23
     * @version: release_v1.5.30
     */
    @PostMapping("/video/control/queryPlatFormLockInfo")
    Result queryPlatFormLockInfo(@RequestBody VideoBaseControlReq videoBaseControlReq);


    /**
     * 获取登录人云台操控权限级别
     * @param
     * @return
     */
    @PostMapping("/video/control/getControlPriority")
    Result<String> getControlPriority(@RequestBody VideoBaseControlReq req);
}