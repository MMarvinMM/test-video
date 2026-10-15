package com.chinatower.provider.call.video.client;

import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.product.api.modules.fallback.CallPresetServiceNewClientFallback;
import com.chinatower.provider.call.video.client.interceptor.FeignNewConfiguration;
import com.chinatower.video.api.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(
        name = "video-video",
        configuration = FeignNewConfiguration.class,
        fallbackFactory = CallPresetServiceNewClientFallback.class
)
public interface PresetNewClient {

    /**
     * @description: 新增预置点
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/preset/addCameraPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=addCameraPreset", "X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public ResultEntity addCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                          @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                          @PathVariable("userCode") String userCode, @RequestBody AddPresetReq addPresetReq);

    /**
     * @description: 删除预置点
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/preset/{presetId}/delCameraPreset",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=delCameraPreset","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public Result<String> delCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                          @PathVariable("presetId") Long presetId, @PathVariable("appCode") String appCode,
                                          @PathVariable("customerCode") String customerCode, @PathVariable("userCode") String userCode,
                                          @RequestBody DelPresetReq delPresetReq);

    /**
     * @description: 修改预置点
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/updatePresetInfo",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=updatePresetInfo","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public Result<String> updatePresetInfo(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                           @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                           @PathVariable("userCode") String userCode, @RequestBody ModPresetReq modPresetReq);



    /**
     * @description: 查询预置点列表
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(
            value = "/preset/devices/{deviceCode}/channels/{channelCode}/queryPresetList",
            method = RequestMethod.POST,
            consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryPresetList","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public Result<List<QueryPresetListData>> queryPresetList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                                             @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                                             @PathVariable("userCode") String userCode, @RequestBody VideoBaseReq videoBaseReq);


    /**
     * @description: 查询通道的录像列表
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(value = "/devices/{deviceCode}/channels/{channelCode}/queryVideoList",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryVideoList","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public Result<QueryVideoData> queryVideoList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                                 @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                                 @PathVariable("userCode") String userCode, @RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq);

    /**
     * @description: 查询PTZ位置
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/queryPtzPosition",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=queryPtzPosition","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public Result<PresetData> queryPtzPosition(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                               @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                               @PathVariable("userCode") String userCode, @RequestBody VideoBaseReq videoBaseReq);

    /**
     * @description: 转到预置点
     * @param:
     * @return:
     * @author: wanglei
     * @date: 2021/11/21 11:07
     */
    @RequestMapping(value = "/preset/devices/{deviceCode}/channels/{channelCode}/doControlPreset",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=doControlPreset","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public ResultEntity doControlPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                         @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                         @PathVariable("userCode") String userCode, @RequestBody DoControlPresetReq doControlPresetReq);


    /**
     * @description: 开始/结束云台转动
     * @param:
     * @return:
     * @author: hued
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzCmds",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=doPtzCmds","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public ResultEntity doPtzCmds(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                               @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                               @PathVariable("userCode") String userCode, @RequestBody DoPtzCmdsReq doPtzCmdsReq);

    /**
     * @description: 开始/结束聚焦(focus)/缩放(zoom)
     * @param:
     * @return:
     * @author: hued
     * @date: 2022/9/10 10:51
     */
    @RequestMapping(value = "/plat/devices/{deviceCode}/channels/{channelCode}/camera/doCameraFocusOrZoom",
            method = RequestMethod.POST,consumes = "application/json",
            headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                    "X-ServerCode=doPtzCmds","X-AppCode={appCode}",
                    "X-deviceCode={deviceCode}"})
    public ResultEntity doCameraFocusOrZoom(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                            @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                            @PathVariable("userCode") String userCode, @RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq);


    /**
     * @description: 转到指定PTZ位置
     * @param:
     * @return:
     * @author: wangyt
     * @date: 2022/9/10 10:51
     */
    @RequestMapping( value = "/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzLocationCmd",
                    method = RequestMethod.POST,
                    consumes = "application/json",
                    headers = {"X-CustomerCode={customerCode}","X-UserCode={userCode}",
                            "X-ServerCode=queryPresetList","X-AppCode={appCode}",
                            "X-deviceCode={deviceCode}"})
    public Result doPtzLocationCmd(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode,
                                     @PathVariable("appCode") String appCode, @PathVariable("customerCode") String customerCode,
                                     @PathVariable("userCode") String userCode, @RequestBody DoPtzLocationCmdReq doControlPresetReq);
}
