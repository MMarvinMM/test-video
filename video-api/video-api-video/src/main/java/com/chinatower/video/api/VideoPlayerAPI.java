package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.deprecated.VideoPlayerAPIMigrate;
import com.chinatower.video.api.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
public class VideoPlayerAPI {

    @Resource
    VideoPlayerAPIMigrate videoPlayerAPIMigrate;

    @Resource
    VideoPlayerStduAPI videoPlayerStduAPI;

    /**
     * 查询通道的录像列表
     * @apiNote 查询通道的录像列表
     * @apiVersion 13.14
     * @tag 视频能力
     * @requestDemo
     * {
     * "deviceCode":"33060200001327000016",
     * "channelCode":"330602330000000191310000007",
     * 	"pageSize": 1,
     * 	"currentPage": 1,
     * 	"storageType": 1,
     * 	"recordType": 2,
     * 	"startTime": "2024-12-20 00:00:00",
     * 	"endTime": "2024-12-20 17:01:59"
     * }
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 	videos：文件列表
     * 		fileName：文件名称
     * 		startTime：开始时间
     * 		endTime：结束时间
     * 		recordType：录像类型
     * 		fileCode：文件唯一编码
     * 		fileImageUrl：图片缩略图
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648458254956,
     * 	"data": {
     * 		"totalPage": 1,
     * 		"totalNum": 3,
     * 		"videos": [
     *                        {
     * 				"fileName": "/00/N01092926",
     * 				"startTime": "2020-09-14 09:29:26",
     * 				"endTime": "2020-09-14 09:29:26",
     * 				"recordType": 0,
     * 				"fileCode": "fererefegege6891",
     * 				"fileImageUrl": "https://v2/data1/image/2.jpg"
     *            },
     *            {
     * 				"fileName": "00/N01103004",
     * 				"startTime": "2020-09-14 09:29:26",
     * 				"endTime": "2020-09-14 09:29:26",
     * 				"recordType": 0,
     * 				"fileCode": "fererefegege1111",
     * 				"fileImageUrl": " https://v2/data1/image/3.jpg"
     *            }
     * 		]
     * 	}
     * }
     * @param videoPlayerQueryVideoListReq
     * @return
     */
    @PostMapping("/video/player/sdk/queryVideoList")
    public Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
        return videoPlayerStduAPI.queryVideoList(videoPlayerQueryVideoListReq);
    }

    /**
     * 获取通道的录像回放流URL地址
     * @apiNote 获取通道的录像回放流URL地址
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		streamUrl：回放流url地址
     * 		streamUuid：本次流的UUID
     * 		streamControlId：倍速回放控制唯一标示
     * @apiVersion 13.15
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "44140300831327000028+44140300831187000001",
     * 	"channelCode": "44140300001180000044",
     * 	"customerCode": "120013349",
     * 	"startTime": "2023-08-16 00:00:57",
     * 	"endTime": "2023-08-16 23:59:59",
     * 	"protocolType": "9",
     * 	"storageType": 1,
     * 	"netType": 2
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *      streamUrl：回放流url地址
     * 		streamUuid：本次流的UUID
     * 		streamControlId：倍速回放控制唯一标示
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1692171013149,
     *     "data": {
     *         "streamUrl": "wss://sp-stdu012113.chinatowercom.cn:10025/vod/238049329.flv",
     *         "streamUuid": "e2a939e43bc94b9396f002bf87517746",
     *         "streamControlId": null
     *     }
     * }
     * @param playerGetVideoPlayBackUrlReq
     * @return
     */
    @PostMapping("/video/player/sdk/getVideoPlaybackUrl")
    public Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq){
        return videoPlayerStduAPI.getVideoPlaybackUrl(playerGetVideoPlayBackUrlReq);
    }

    /**
     * 获取通道实时流URL地址
     * @apiNote 获取通道实时流URL地址
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		streamUrl：流url地址
     * 		streamUuid：本次流的UUID
     * @apiVersion 13.16
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "44140300831327000045+44140300831187000001",
     * 	"channelCode": "44140300001180000022",
     * 	"customerCode": "120013349",
     * 	"streamType": 1,
     * 	"protocolType": 9,
     * 	"netType": 2
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * streamUrl流url地址
     * streamUuid本次流的UUID
     * streamType码流类型
     * streamSwitchFlag切换主子码流 2-不可切换
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1692171259268,
     *     "data": {
     *         "streamUrl": "wss://sp-stdu084185.chinatowercom.cn:10077/live/44140300831187000001_18_0_673f2f8dfede47a6b55f43ac498687e7.flv",
     *         "streamUuid": "673f2f8dfede47a6b55f43ac498687e7",
     *         "streamType": 1,
     *         "streamSwitchFlag": 1
     *     }
     * }
     * @param playerGetVideoRealtimeUrlReq
     * @return
     */
    @PostMapping("/video/player/sdk/getVideoRealtimeUrl")
    public Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq){
        return videoPlayerStduAPI.getVideoRealtimeUrl(playerGetVideoRealtimeUrlReq);
    }

    /**
     * 3D定位
     * @apiNote 3D定位
     * @apiVersion 13.17
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "44140300831327000045+44140300831187000001",
     * 	"channelCode": "44140300001180000022",
     * 	"customerCode": "120013349",
     * 	"windowLength": 769,
     * 	"windowWidth": 356,
     * 	"midPointX": 419,
     * 	"midPointY": 193,
     * 	"lengthX": 373,
     * 	"lengthY": 44,
     * 	"zoomFlag": true
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
     * 	"time": 1692176750509
     * }
     * @param playerThirdLocationReq
     * @return
     */
    @PostMapping("/video/player/sdk/thirdLocation")
    public Result thirdLocation(@RequestBody PlayerThirdLocationReq playerThirdLocationReq){
        return videoPlayerAPIMigrate.thirdLocation(playerThirdLocationReq);
    }

    /**
     * 获取语音对讲通道
     * @apiNote 获取语音对讲通道
     * @apiVersion 13.18
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCode": "11010800831327000008",
     * 	"plugId": "xxx",
     * 	"netType": 1
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 流url地址
     * {
     * 	"code": 200,
     * 	"msg": "成功",
     * 	"time": 1692176750509
     * 	"data": {
     *       "streamUrl": "wss://sl-hunan-stud-13.chinatowercom.cn:10028/135041228",
     *     }
     * }
     * @param playerGetAudioChannelReq
     * @return
     */
    @PostMapping("/video/player/sdk/getAudioChannel")
    public Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq){
        return videoPlayerStduAPI.getAudioChannel(playerGetAudioChannelReq);
    }

    /**
     * 抓图
     * @apiNote 抓图
     * @apiVersion 13.20
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCode": "11010800831327000008",
     * 	"streamType": 1,
     * 	"imageUpUrl": "xxx",
     * 	"imageName": "yyy"
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
     * 	"time": 1692176750509
     * }
     * @param playerSnapShotReq
     * @return
     */
    @PostMapping("/video/player/sdk/snapShot")
    public Result snapShot(@RequestBody PlayerSnapShotReq playerSnapShotReq){
        return videoPlayerAPIMigrate.snapShot(playerSnapShotReq);
    }

    /**
     * 倍速回放
     * @apiNote 倍速回放
     * @apiVersion 13.19
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCode": "11010800831327000008",
     * 	"streamControlId": "Uint16Array",
     * 	"controlType": 1,
     * 	"speed": 1
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
     * 	"time": 1692176750509
     * }
     * @param playerPlayBackControlReq
     * @return
     */
    @PostMapping("/video/player/sdk/playBackControl")
    public Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq){
        return videoPlayerStduAPI.playBackControl(playerPlayBackControlReq);
    }


    /**
     * 获取视频存储时长
     * @apiNote 获取视频存储时长
     * @apiVersion 13.47
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"customerCode": "122035807",
     * 	"tenantId": "122035807",
     * 	"deviceCode": "43062400831327000001",
     * 	"channelCode": "43062400001130000001"
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
     * 	"msg": "请求成功",
     * 	"time": 1694748422696,
     * 	"data": "7"
     * }
     * @param channelStorageTimeReq
     * @return
     */
    @PostMapping("/video/player/queryRecordPlanStorageDay")
    public Result<ChannelStorageTimeReq> queryRecordPlanStorageDay(@RequestBody ChannelStorageTimeReq channelStorageTimeReq){
        return videoPlayerAPIMigrate.queryRecordPlanStorageDay(channelStorageTimeReq);
    }


    /**
     * 视频管理-批量开始下载
     * @apiNote 批量开始下载
     * @apiVersion 13.57
     * @tag 视频能力
     * @requestDemo
     * [
     *     {
     *         "startTime": "2024-01-22 15:00:00",
     *         "endTime": "2024-01-22 15:30:00",
     *         "storageType": 1,		//1-中心存储  0-前端存储
     *         "deviceCode": "11011500001327000012",
     *         "channelCode": "11011500001310000001",
     *         "batchId": "1223dnm1p534g42cwe22"	//时间戳+uuid 前端生成
     *     }
     * ]
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *   设备编码
     *   通道编码
     *   批次id
     *   返回码
     *   返回信息
     *   下载进度
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1705989568786,
     *     "data": [
     *         {
     *             "deviceCode": "11011500001327000012",
     *             "channelCode": "11011500001310000001",
     *             "batchId": "1223dnm1p534g42cwe22",
     *             "returnCode": "0000",
     *             "returnMsg": "ok",
     *             "downloadProcessDTO": null
     *         }
     *     ]
     * }
     * @param list
     * @return
     */
    @PostMapping("/video/player/downloadVideo")
    public Result<List<VideoDownloadDTO>> downloadVideo(@RequestBody List<VideoDownloadVO> list){
        return videoPlayerStduAPI.downloadVideo(list);
    }

    /**
     * 视频管理-批量停止下载
     * @apiNote 视频管理-批量停止下载
     * @apiVersion 13.58
     * @tag 视频能力
     * @requestDemo
     * [
     *     {
     *         "startTime": "2024-01-22 15:00:00",
     *         "endTime": "2024-01-22 15:30:00",
     *         "storageType": 1,		//1-中心存储  0-前端存储
     *         "deviceCode": "11011500001327000012",
     *         "channelCode": "11011500001310000001",
     *         "batchId": "1223dnm1p534g42cwe22"	//时间戳+uuid 前端生成
     *     }
     * ]
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *   设备编码
     *   通道编码
     *   批次id
     *   返回码
     *   返回信息
     *   下载进度
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1705980236947,
     *     "data": [
     *         {
     *             "deviceCode": "11011500001327000012",
     *             "channelCode": "11011500001310000001",
     *             "batchId": "1223dnm1p534go2e3rg3u2f2342cwe22",
     *             "returnCode": "0000",
     *             "returnMsg": "ok",
     *             "downloadProcessDTO": null
     *         }
     *     ]
     * }
     * @param list
     * @return
     */
    @PostMapping("/video/player/stopDownloadVideo")
    public Result<List<VideoDownloadDTO>> stopDownloadVideo(@RequestBody List<VideoDownloadVO> list){
        return videoPlayerStduAPI.stopDownloadVideo(list);
    }


    /**
     * 功能说明: 视频管理-录像下载进度百分比查询
     * @apiNote 视频管理-录像下载进度百分比查询
     * @apiVersion 13.56
     * @tag 视频能力
     * @requestDemo
     * [
     *     {
     *         "startTime": "2024-01-22 15:00:00",
     *         "endTime": "2024-01-22 15:30:00",
     *         "storageType": 1,
     *         "deviceCode": "11011500001327000012",
     *         "channelCode": "11011500001310000001",
     *         "batchId": "1223dnm1p534g42cwe22"
     *     }
     * ]
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *   batchId批次id
     *   process下载进度
     *   downloadUrl下载url
     *   fileName文件名称
     *   order下载顺序
     *   processCode下载状态
     *   fileSize文件大小
     *   deviceCode设备编码
     *   channelCode通道编码
     *   startTime开始时间
     *   endTime结束时间
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1705995588802,
     *     "data": {
     *         "z23dsdeasllj32fsa2ed": {
     *             "batchId": "z23dsdeasllj32fsa2ed",
     *             "process": "31%",
     *             "downloadUrl": "",
     *             "fileName": "",
     *             "order": -1,
     *             "processCode": "1000",//8888-下载失败  1000-正常
     *             "fileSize": 48,//单位MB
     *             "deviceCode": "51150200831327000044",
     *             "channelCode": "51150200001310000001",
     *             "startTime": "2024-01-23 15:30:00",
     *             "endTime": "2024-01-23 15:35:00"
     *         }
     *     }
     * }
     * @param list
     * @return
     */
    @PostMapping("/video/player/downloadProcess")
    public Result<Map<String, DownloadProcessDTO>> downloadProcess(@RequestBody List<VideoDownloadVO> list){
        return videoPlayerStduAPI.downloadProcess(list);
    }

    @PostMapping("/video/player/snapShot")
    public Result snapShotByNode(@RequestBody PlayerSnapShotReq playerSnapShotReq) {
        return videoPlayerAPIMigrate.snapShotByNode(playerSnapShotReq);
    }

}