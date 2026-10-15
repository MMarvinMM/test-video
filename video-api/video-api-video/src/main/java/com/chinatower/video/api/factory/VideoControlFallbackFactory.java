package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 视频控制降级处理
 *
 * @Author: zhujun
 * @Date: 2022/1/16
 */
@Component
public class VideoControlFallbackFactory implements FallbackFactory<VideoControlAPI> {
    private static final Logger log = LoggerFactory.getLogger(VideoControlFallbackFactory.class);

    @Override
    public VideoControlAPI create(Throwable throwable) {
        log.error("调用视频控制接口失败:{}", throwable.getMessage());
        throwable.printStackTrace();
        return new VideoControlAPI() {
            @Override
            public Result doPatrol(DoPatrolReq doPatrolReq) {
                return Result.fail("调用启用/停用巡航路径方法失败:" + throwable.getMessage());
            }

            @Override
            public Result doPtzCmds(DoPtzCmdsReq doPtzCmdsReq) {
                return Result.fail("调用视频开始结束云台转动方法失败:" + throwable.getMessage());
            }

            @Override
            public Result doCameraFocusOrZoom(DoCameraFocusOrZoomReq doCameraFocusOrZoomReq) {
                return Result.fail("调用视频开始/结束聚焦缩放方法失败:" + throwable.getMessage());
            }

            @Override
            public Result doPtzLocationCmd(DoPtzLocationCmdReq doPtzLocationCmdReq) {
                return Result.fail("调用转到指定PTZ位置方法失败:" + throwable.getMessage());
            }

            @Override
            public Result doControlPreset(DoControlPresetReq doControlPresetReq) {
                return Result.fail("调用转到预置点方法失败:" + throwable.getMessage());
            }

            @Override
            public Result doLock(VideoBaseControlReq videoBaseControlReq) {
                return Result.fail("调用获取云台控制权失败:" + throwable.getMessage());
            }

            @Override
            public Result doRelease(VideoBaseControlReq videoBaseControlReq) {
                return Result.fail("调用释放云台控制权失败:" + throwable.getMessage());
            }

            @Override
            public Result doSwitchLight(PlayerDoSwitchLightReq playerDoSwitchLightReq) {
                return Result.fail("调用开启/关闭灯失败:" + throwable.getMessage());
            }

            @Override
            public Result doSetChannelParam(DoSetChannelParamReq doSetChannelParamReq) {
                return Result.fail("调用设置通道参数失败:" + throwable.getMessage());
            }

            @Override
            public Result<PresetData> turnToPtzByPointAngle(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
                return Result.fail("根据定位点转动Ptz:" + throwable.getMessage());
            }
            @Override
            public Result<List<DevicePointsValidVO>> pointsValidBatch(@RequestBody List<String> list){
                return Result.fail("批量判断摄像机是否有有效参考点:" + throwable.getMessage());
            }
            @Override
            public Result doControlPresetNoRequest(DoControlPresetReq doControlPresetReq) {
                return Result.fail("调用转到预置点方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<PresetData> turnToPtzByPointAngleFly(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
                return Result.fail("根据定位点+无人机飞行高度，计算ptz:" + throwable.getMessage());
            }
            @Override
            public Result<String> getControlPriority(VideoBaseControlReq req) {
                return Result.fail("获取登录人云台操控权限级别异常:" + throwable.getMessage());
            }

            @Override
            public Result<PresetData> queryPtzSdk(VideoBaseReq videoBaseReq) {
                return Result.fail("获取摄像机ptz信息异常:" + throwable.getMessage());
            }

            @Override
            public Result<VideoPtzRes> getPercentPtzByMassPointBatch(VideoPtzReq videoPtzReq) {
                return Result.fail("视图联动，以地找视频异常:" + throwable.getMessage());
            }
        };
    }
}
