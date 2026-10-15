package com.chinatower.product.api.modules.devices.api;

import com.chinatower.product.api.modules.common.RespResult;
import com.chinatower.video.api.entity.DoCameraFocusOrZoomReq;
import com.chinatower.video.api.entity.DoPtzCmdsReq;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DevicesAPI
 * @Description: 监控服务接口
 * @Author: zzh
 * @Date: 2021/9/27 15:25
 **/
public interface DevicesAPI {

    /**
     * 获取通道的录像回放流URL地址
     * @param deviceCode
     * @param channelCode
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/getVideoPlaybackUrl")
    RespResult getVideoPlaybackUrl(String deviceCode, String channelCode, Map map, HttpServletRequest request);

    /**
     * 获取通道实时流URL地址
     * @param deviceCode
     * @param channelCode
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/getVideoRealtimeUrl")
    RespResult getVideoRealtimeUrl(String deviceCode, String channelCode, Map map, HttpServletRequest request);

    /**
     * 开始/结束云台转动
     * @param deviceCode
     * @param channelCode
     * @param doPtzCmdsReq
     * @return
     */
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzCmds")
    RespResult doPtzCmds(String deviceCode, String channelCode, DoPtzCmdsReq doPtzCmdsReq, HttpServletRequest request);

    /**
     * 开始/结束聚焦(focus)/缩放(zoom)
     * @param deviceCode
     * @param channelCode
     * @param doCameraFocusOrZoomReq
     * @return
     */
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/camera/doCameraFocusOrZoom")
    RespResult doCameraFocusOrZoom(String deviceCode, String channelCode, DoCameraFocusOrZoomReq doCameraFocusOrZoomReq, HttpServletRequest request);

    /**
     * 批量获取通道实时流URL地址
     * @param list
     * @param request
     * @return
     */
    @PostMapping("/devices/channels/getVideoRealtimeUrls")
    RespResult getVideoRealtimeUrls(List list, HttpServletRequest request);
}
