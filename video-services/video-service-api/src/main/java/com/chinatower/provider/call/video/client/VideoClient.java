package com.chinatower.provider.call.video.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.product.api.modules.fallback.CallVideoServiceClientFallback;
import com.chinatower.provider.call.video.client.interceptor.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 运营子平台服务
 * @param:
 * @return:
 * @author: hued@strongdata.com.cn
 * @date: 2021年5月6日13:57:21
 */
@FeignClient(
        name = "YUNGUAN",
        url = "${yy.gateway.url}",
        configuration = FeignConfiguration.class
        ,fallbackFactory = CallVideoServiceClientFallback.class
)
public interface VideoClient {

    @RequestMapping(
            value = "/cruise/devices/{deviceCode}/channels/{channelCode}/addCruisePatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=addCruisePatrol","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject addCruisePatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                               @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                               @PathVariable("orderCode") String orderCode,
                               @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/devices/{deviceCode}/channels/{channelCode}/delPatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=delPatrol","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject delPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                         @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                         @PathVariable("orderCode") String orderCode,
                         @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/batchDelPatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=batchDelPatrol","X-OrderCode={orderCode}"})
    JSONObject batchDelPatrol(@PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                              @PathVariable("orderCode") String orderCode,
                              @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/devices/{deviceCode}/channels/{channelCode}/updatePatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=updateCruisePatrol","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject updatePatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                            @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                            @PathVariable("orderCode") String orderCode,
                            @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/devices/{deviceCode}/channels/{channelCode}/queryPatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryPatrol","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject queryPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                           @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                           @PathVariable("orderCode") String orderCode,
                           @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/devices/{deviceCode}/channels/{channelCode}/doPatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=doPatrol","X-LockToken={lockToken}","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                        @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                        @PathVariable("lockToken") String lockToken,
                        @PathVariable("orderCode") String orderCode,
                        @RequestBody JSONObject inJson);

    @RequestMapping(value = "/cruise/batchStartOrEndPatrol",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=batchStartOrEndPatrol","X-OrderCode={orderCode}"})
    JSONObject batchStartOrEndPatrol(@PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                     @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);

    @PostMapping("/v1/common/queryOrderList")
    JSONObject queryOrderList(@RequestBody JSONObject inJson);

    @RequestMapping(
            value = "/v1/common/callback/doRegister",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}"}
    )
    JSONObject doRegister(@PathVariable("customerCode") String customerCode,
                          @RequestBody JSONObject inJson);

    @RequestMapping(
            value = "/v1/common/callback/doUnregister",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}"}
    )
    JSONObject doUnregister(@PathVariable("customerCode") String customerCode,
                            @RequestBody JSONObject inJson);


    /**
     * 4.5.	云台加锁申请
     * @param deviceCode
     * @param channelCode
     * @param
     * @return
     */
    @RequestMapping(value = "/v1/common/lock/devices/{deviceCode}/channels/{channelCode}/doLock",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-OrderCode={orderCode}","X-CustomerCode={customerCode}","X-ServerCode=doLock",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doLock(@PathVariable("deviceCode") String deviceCode,
                      @PathVariable("channelCode") String channelCode,
                      @PathVariable("orderCode") String orderCode,
                      @PathVariable("customerCode") String customerCode);

    /**
     * 4.6.	云台锁释放
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/v1/common/lock/devices/{deviceCode}/channels/{channelCode}/doRelease",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-LockToken={lockToken}","X-CustomerCode={customerCode}","X-ServerCode=doRelease",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doRelease(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                         @PathVariable("lockToken") String lockToken,
                         @PathVariable("customerCode") String customerCode);

    /**
     * 4.7.	查询设备通道信息
     * @return
     */
    @RequestMapping(value = "/v1/common/queryDeviceList",
            method = RequestMethod.POST,consumes = "application/json")
    JSONObject queryDeviceList(@RequestBody JSONObject json);

    /**
     * 4.8.	获取站址点位信息
     * @return
     */
    @RequestMapping(value = "/v1/common/querySites",
            method = RequestMethod.POST,consumes = "application/json")
    JSONObject querySites(@RequestBody JSONObject json);


    /**
     * 6.6.	获取通道的录像回放流URL地址
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/devices/{deviceCode}/channels/{channelCode}/getVideoPlaybackUrl",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-StreamUuid={streamUuid}","X-OrderCode={orderCode}",
                    "X-CustomerCode={customerCode}","X-ServerCode=getVideoPlaybackUrl",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}",
                    "X-PlugId={plugId}"})
    JSONObject getVideoPlaybackUrl(@PathVariable("deviceCode") String deviceCode,
                                   @PathVariable("channelCode") String channelCode,
                                   @PathVariable("streamUuid") String streamUuid,
                                   @PathVariable("orderCode") String orderCode,
                                   @PathVariable("customerCode") String customerCode,
                                   @PathVariable("plugId") String plugId, @RequestBody JSONObject json);


    /**
     * 6.7.	获取通道实时放流URL地址
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/devices/{deviceCode}/channels/{channelCode}/getVideoRealtimeUrl",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-StreamUuid={streamUuid}","X-OrderCode={orderCode}",
                    "X-CustomerCode={customerCode}","X-ServerCode=getVideoRealtimeUrl",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}",
                    "X-PlugId={plugId}"})
    JSONObject getVideoRealtimeUrl(@PathVariable("deviceCode") String deviceCode,
                                   @PathVariable("channelCode") String channelCode,
                                   @PathVariable("streamUuid") String streamUuid,
                                   @PathVariable("orderCode") String orderCode,
                                   @PathVariable("customerCode") String customerCode,
                                   @PathVariable("plugId") String plugId, @RequestBody JSONObject json);

    /**
     * 6.49	批量获取通道实时流URL地址
     * @param json
     * @return
     */
    @RequestMapping(value = "/devices/channels/getVideoRealtimeUrls",
            method = RequestMethod.POST,consumes = "application/json")
    JSONObject getVideoRealtimeUrls(@RequestBody JSONObject json);


    /**
     * 6.13.	查询事件告警详情
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/alarm/devices/{deviceCode}/channels/{channelCode}/alarm/{alarmId}/getAlarmInfo",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-OrderCode={orderCode}","X-CustomerCode={customerCode}","X-ServerCode=getAlarmInfo",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject getAlarmInfo(@PathVariable("deviceCode") String deviceCode,
                            @PathVariable("channelCode") String channelCode,
                            @PathVariable("alarmId") String alarmId);


    /**
     * 6.14.	开始/结束云台转动
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzCmds",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-OrderCode={orderCode}",
                    "X-CustomerCode={customerCode}","X-LockToken={lockToken}","X-ServerCode=doPtzCmds",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doPtzCmds(@PathVariable("deviceCode") String deviceCode,
                         @PathVariable("channelCode") String channelCode,
                         @PathVariable("orderCode") String orderCode,
                         @PathVariable("customerCode") String customerCode,
                         @PathVariable("lockToken") String lockToken, @RequestBody JSONObject json);


    /**
     * 6.18.	开始/结束聚焦(focus)/缩放(zoom)
     * @param deviceCode
     * @param channelCode
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/camera/doCameraFocusOrZoom",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-OrderCode={orderCode}",
                    "X-CustomerCode={customerCode}","X-LockToken={lockToken}","X-ServerCode=doCameraFocusOrZoom",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doCameraFocusOrZoom(@PathVariable("deviceCode") String deviceCode,
                                   @PathVariable("channelCode") String channelCode,
                                   @PathVariable("orderCode") String orderCode,
                                   @PathVariable("customerCode") String customerCode,
                                   @PathVariable("lockToken") String lockToken, @RequestBody JSONObject json);


    /**
     * @description: 增加预置点
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:50
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/preset/addCameraPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=addCameraPreset","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject addCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                      @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                      @PathVariable("orderCode") String orderCode,
                                      @RequestBody JSONObject inJson);

    /**
     * @description: 删除预置点
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/preset/{presetId}/delCameraPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=delCameraPreset","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject delCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                      @PathVariable("presetId") Integer presetId,
                                      @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                      @PathVariable("orderCode") String orderCode);

     /**
       * @description: 批量删除预置点
       * @param:
       * @return:
       * @author: shenyq@strongdata.com.cn
       * @date: 2021/9/21 10:24
       */
    @RequestMapping(
            value = "/preset/batchDelCameraPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=batchDelCameraPreset","X-OrderCode={orderCode}"})
    public JSONObject batchDelCameraPreset(@PathVariable("presetId") int[] presets, @PathVariable("orderCode") String orderCode,
                                           @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode);


    /**
     * @description: 修改预置点
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/updatePresetInfo",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=updatePresetInfo","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject updatePresetInfo(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                       @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                       @PathVariable("orderCode") String orderCode,
                                       @RequestBody JSONObject inJson);

    /**
     * @description: 查询预置点列表
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/queryPresetList",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryPresetList","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject queryPresetList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                      @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                      @PathVariable("orderCode") String orderCode);

    /**
     * @description: 转到预置点
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:52
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/doControlPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-OrderCode={orderCode}",
                    "X-CustomerCode={customerCode}","X-LockToken={lockToken}","X-ServerCode=doControlPreset",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject doControlPreset(@PathVariable("deviceCode") String deviceCode,
                                      @PathVariable("channelCode") String channelCode,
                                      @PathVariable("orderCode") String orderCode,
                                      @PathVariable("customerCode") String customerCode,
                                      @PathVariable("lockToken") String lockToken, @RequestBody JSONObject json);

    /**
     * @description: 查询看守位列表
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 10:56
     */
    @RequestMapping(
            value = "/guard/devices/{deviceCode}/channels/{channelCode}/queryGuardList",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryGuardList","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public String queryGuardList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                 @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                 @PathVariable("orderCode") String orderCode);

    /**
     * @description: 设置看守位
     * @param:
     * @return:
     * @author: shenyq@strongdata.com.cn
     * @date: 2021/5/10 11:09
     */
    @RequestMapping(
            value = "/guard/devices/{deviceCode}/channels/{channelCode}/doSetGuard",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-OrderCode={orderCode}","X-UserCode={userCode}",
                    "X-CustomerCode={customerCode}","X-LockToken={lockToken}","X-ServerCode=doSetGuard",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public String doSetGuard(@PathVariable("deviceCode") String deviceCode,
                             @PathVariable("channelCode") String channelCode,
                             @PathVariable("orderCode") String orderCode,
                             @PathVariable("userCode") String userCode,
                             @PathVariable("customerCode") String customerCode,
                             @PathVariable("lockToken") String lockToken, @RequestBody JSONObject json);


    /**
     * 4.2.	获取行政区域树
     * @param inJson
     * @author: liuxt
     * @date: 2021/5/10
     * @return
     */
    @RequestMapping(value = "/v1/common/getRegionList",
            method = RequestMethod.POST,consumes = "application/json")
    JSONObject getRegionList(@RequestBody JSONObject inJson);


    /**
     * 6.3.	查询通道的录像列表
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/devices/{deviceCode}/channels/{channelCode}/queryVideoList",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryVideoList","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject queryVideoList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                              @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);


    /**
     * 6.15. 查询PTZ位置(绝对位置）
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/queryPtzPosition",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryPtzPosition","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject queryPtzPosition(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                @PathVariable("orderCode") String orderCode);


    /**
     * 6.16.	转到指定PTZ位置(绝对位置）
     * @param deviceCode
     * @param channelCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzLocationCmd",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=doPtzLocationCmd","X-OrderCode={orderCode}","X-LockToken={lockToken}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject doPtzLocationCmd(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                @PathVariable("orderCode") String orderCode, @PathVariable("lockToken") String lockToken, @RequestBody JSONObject inJson);


    /**
     * 6.17.	查询当前聚焦参数(focus) /缩放(zoom)
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/camera/getCameraFocusOrZoomInfo",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=getCameraFocusOrZoomInfo","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject getCameraFocusOrZoomInfo(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                        @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                        @PathVariable("orderCode") String orderCode);


    /**
     * 6.5.	获取通道抓拍图片URL
     * @param inJson
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/devices/{deviceCode}/channels/{channelCode}/capturePictUrl",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=capturePictUrl","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject capturePictUrl(@PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                              @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);


    /**
     * 6.9.	新增通道录像计划
     * @param inJson
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plan/devices/{deviceCode}/channels/{channelCode}/addRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=addRecordPlan","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject addRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                             @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                             @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);


    /**
     * 6.10.	修改通道录像计划
     * @param inJson
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plan/devices/{deviceCode}/channels/{channelCode}/updateRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=updateRecordPlan","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject updateRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);


    /**
     * 6.11.	删除通道录像计划
     * @param inJson
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plan/devices/{deviceCode}/channels/{channelCode}/deleteRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=deleteRecordPlan","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject deleteRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);

    @RequestMapping(value = "/plan/batchDeleteRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=batchDeleteRecordPlan","X-OrderCode={orderCode}"})
    JSONObject batchDeleteRecordPlan(@PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                     @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);

    @RequestMapping(value = "/plan/batchStartOrEndRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=batchStartOrEndRecordPlan","X-OrderCode={orderCode}"})
    JSONObject batchStartOrEndRecordPlan(@PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                         @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);

    /**
     * 6.12.	查询通道录像计划
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/5/11
     * @return
     */
    @RequestMapping(value = "/plan/devices/{deviceCode}/channels/{channelCode}/queryRecordPlan",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryRecordPlan","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject queryRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                               @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                               @PathVariable("orderCode") String orderCode);

    /**
     * @description: 6.35.	设备/通道/预置点/监控区域/算法关系推送
     * @param:
     * @return:
     * @author: liujb@strongdata.com.cn
     * @date: 2021/6/20
     */
    @RequestMapping(
            value = "/service/center/devices/channels/synchDeviceAlgorithms",
            method = RequestMethod.POST,
            consumes = "application/json")
    public JSONObject synchDeviceAlgorithms(@RequestBody JSONArray json);


//    @RequestMapping(
//            value = "/service/center/devices/{deviceCode}/channels/{channelCode}/synchDeviceAlgorithms",
//            method = RequestMethod.POST,
//            consumes = "application/json",
//            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
//                    "X-ServerCode=synchDeviceAlgorithms","X-OrderCode={orderCode}",
//                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
//    public JSONObject synchDeviceAlgorithms(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
//                                            @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
//                                            @PathVariable("orderCode") String orderCode, @RequestBody JSONArray json);


    /**
     * 6.36.	3D定位
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/7/20
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/thirdLocation",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=thirdLocation","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    JSONObject thirdLocation(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                             @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                             @PathVariable("orderCode") String orderCode, @RequestBody JSONObject inJson);



    /**
     * 6.37.	 获取语音对讲通道
     * @param deviceCode
     * @param channelCode
     * @author: liuxt
     * @date: 2021/7/20
     * @return
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/getAudioChannel",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=getAudioChannel","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}",
                    "X-PlugId={plugId}"})
    JSONObject getAudioChannel(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                               @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                               @PathVariable("orderCode") String orderCode, @PathVariable("plugId") String plugId, @RequestBody JSONObject inJson);
    
    /**
     * @description: 6.38抓图
     * @param:
     * @return:
     * @author: dengyl@strongdata.com.cn
     * @date: 2021/11/2 10:50
     */
    @RequestMapping(
            value = "/plat/devices/{deviceCode}/channels/{channelCode}/snapshot",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=addCameraPreset","X-OrderCode={orderCode}",
                    "X-deviceCode={deviceCode}","X-channelCode={channelCode}"})
    public JSONObject snapshot(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                               @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                               @PathVariable("orderCode") String orderCode,
                               @RequestBody JSONObject inJson);
    
}
