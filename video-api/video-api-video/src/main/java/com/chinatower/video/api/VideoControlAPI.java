package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.api.factory.VideoControlFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "VideoControlAPI",
        value = ServiceNameConstants.VIDEO_SERVICE,
        fallbackFactory = VideoControlFallbackFactory.class)
public interface VideoControlAPI {


    /**
     * 获取云台控制权
     * @apiNote 获取云台控制权
     * @apiVersion 5.10
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"duration": 900,
     * 	"token": "sddfssfffsfxsdvvadq"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 返回实体类（com.chinatower.video.api.entity.DoLockOrReleasePtzData）
     * token获取云台控制权后，基础平台返回的令牌token
     * waitDuration等待时长，请求令牌不成功时为必填，单位为秒
     * duration返回锁定成功后可使用的时长，单位：秒
     * queue排队位次
     * wait排队时间，单位：秒
     *
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1648458254956,
     * 	"data": {
     * 		"token": "sddfssfffsfxsdvvadq",
     * 		"waitDuration": 0,
     * 		"duration": 10,
     * 		"queue": 0,
     * 		"wait": 0
     *        }
     * }
     * @param videoBaseControlReq
     * @return
     */
    @PostMapping("/video/control/sdk/doLock")
    Result<DoLockOrReleasePtzData> doLock(@RequestBody VideoBaseControlReq videoBaseControlReq);

    /**
     * 释放云台控制权
     * @apiNote 释放云台控制权
     * @apiVersion 5.11
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"duration": 10,
     * 	"token": "sddfssfffsfxsdvvadq"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     *     "code": 200,
     *     "msg": "成功",
     *     "time": 1648458254956
     * }
     * @param videoBaseControlReq
     * @return
     */
    @PostMapping("/video/control/sdk/doRelease")
    Result doRelease(@RequestBody VideoBaseControlReq videoBaseControlReq);


    /**
     * 启用/停用巡航路径
     */
    @PostMapping("/video/control/sdk/doPatrol")
    Result doPatrol(@RequestBody DoPatrolReq doPatrolReq);


    /**
     * 开始/结束云台转动
     * @apiNote 开始/结束云台转动，先获取控制权
     * @apiVersion 5.12
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"operType": 4,
     * 	"operContent": 1,
     * 	"step": 5,
     * 	"operBatchId": "1111222223333344444",
     * 	"operTimestamp":"1234567890123"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1692155672825
     * }
     * @param doPtzCmdsReq
     * @return
     */
    @PostMapping("/video/control/sdk/doPtzCmds")
    Result doPtzCmds(@RequestBody DoPtzCmdsReq doPtzCmdsReq);


    /**
     * 开始/结束聚焦(focus)/缩放(zoom)
     * @apiNote 开始/结束聚焦(focus)/缩放(zoom)
     * @apiVersion 5.13
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"operType": 11,
     * 	"operContent": 0,
     * 	"step": 5,
     * 	"lockToken": "sddfssfffsfxsdvvadq",
     * 	"operBatchId": "1111222223333344444",
     * 	"operTimestamp":"1234567890123"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1692167691281
     * }
     * @param doCameraFocusOrZoomReq
     * @return
     */
    @PostMapping("/video/control/sdk/doCameraFocusOrZoom")
    Result doCameraFocusOrZoom(@RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq);


    /**
     * 转到指定PTZ位置
     * @apiNote 转到指定PTZ位置
     * @apiVersion 5.14
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"pan": "1200",
     * 	"tilt": "100",
     * 	"zoom": "10",
     * 	"ptzIsEsc":"1"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1692167691281
     * }
     * @param doPtzLocationCmdReq
     * @return
     */
    @PostMapping("/video/control/sdk/doPtzLocationCmd")
    Result doPtzLocationCmd(@RequestBody DoPtzLocationCmdReq doPtzLocationCmdReq);

    /**
     * 转到预置点
     * @apiNote 转到预置点
     * @apiVersion 5.15
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"presetId": 120564,
     * }
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1692167691281
     * }
     * @param doControlPresetReq
     * @return
     */
    @PostMapping("/video/control/sdk/doControlPreset")
    Result doControlPreset(@RequestBody DoControlPresetReq doControlPresetReq);

    /**
     * 转到预置点
     * 上游系统异步调用转向预置点是，取不到当前登录人的token，此接口需要从请求参数中获取
     * @param doControlPresetReq
     * @return
     */
    @PostMapping("/video/control/sdk/doControlPresetNoRequest")
    Result doControlPresetNoRequest(@RequestBody DoControlPresetReq doControlPresetReq);

    /**
     * 设置通道参数
     */
    @PostMapping("/video/control/sdk/doSetChannelParam")
    Result doSetChannelParam(@RequestBody DoSetChannelParamReq doSetChannelParamReq);



    /**
     * 开启/关闭灯
     * @apiNote 开启/关闭灯
     * @apiVersion 5.16
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"switchType": 1,
     * 	"optType": 1
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1694747501314
     * }
     * @param playerDoSwitchLightReq
     * @return
     */
    @PostMapping("/video/control/sdk/doSwitchLight")
    Result doSwitchLight(@RequestBody PlayerDoSwitchLightReq playerDoSwitchLightReq);

    /**
     * 根据定位点转动Ptz
     * @apiNote 根据定位点转动Ptz
     * @apiVersion 5.17
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008",
     * 	"cameraLon": 113.679072,
     * 	"cameraLat": 34.790439,
     * 	"pointLon": 113.679072,
     * 	"pointLat": 34.790439,
     * 	"tenantId": "122035807"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694748223602,
     * 	"data": {}
     * }
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngle")
    Result<PresetData> turnToPtzByPointAngle(@RequestBody VideoPtzQueryReq videoPtzQueryReq);

    @PostMapping("/video/query/pointsValidBatch")
    Result<List<DevicePointsValidVO>> pointsValidBatch(@RequestBody List<String> list);

    /**
     * 根据定位点+无人机飞行高度，计算ptz
     * @apiNote 根据定位点+无人机飞行高度，计算ptz
     * @apiVersion 5.18
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008",
     * 	"cameraLon": 113.679072,
     * 	"cameraLat": 34.790439,
     * 	"pointLon": 113.679072,
     * 	"pointLat": 34.790439,
     * 	"tenantId": "122035807",
     * 	"flyHeight": "11"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1702362973987,
     * 	"data": {}
     * }
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngleFly")
    Result<PresetData> turnToPtzByPointAngleFly(@RequestBody VideoPtzQueryReq videoPtzQueryReq);

    @PostMapping("/video/control/getControlPriority")
    Result<String> getControlPriority(@RequestBody VideoBaseControlReq req);

    /**
     * 查询PTZ
     * @apiNote 查询PTZ
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		pan：平参数[0, 3600]
     * 		tilt：垂直参数[-1800,1800]
     * 		zoom：变倍参数[0, 128]
     * @apiVersion 5.19
     * @tag 五、视频能力
     * @requestDemo
     * {
     *   "deviceCode": "11080200001327000055",
     *   "channelCode": "110802100000000011310000026",
     *   "tenantId":"121062646",
     *   "industryCode":"000000"
     * }
     * @responseDemo
     * pan：平参数[0, 3600]
     * tilt：垂直参数[-1800,1800]
     * zoom：变倍参数[0, 128]
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648458254956,
     * 	"data": {
     * 		"pan": 1800,
     * 		"tilt": 600,
     * 		"zoom": 1
     *        }
     * }
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryPtzSdk")
    public Result<PresetData> queryPtzSdk(@RequestBody VideoBaseReq videoBaseReq);

    /**
     * 视图联动，以地找视频
     * @param videoPtzReq
     * @return
     */
    @PostMapping("/video/query/getPercentPtzByMassPointBatch")
    Result<VideoPtzRes> getPercentPtzByMassPointBatch(@RequestBody VideoPtzReq videoPtzReq);
}