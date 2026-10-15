package com.chinatower.product.api.modules.devices;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.common.RespResult;
import com.chinatower.product.api.modules.devices.api.DevicesAPI;
import com.chinatower.product.api.modules.devices.dao.DevicesDAO;
import com.chinatower.product.api.modules.videosquare.entity.PlatformControlVO;
import com.chinatower.product.api.modules.videosquare.entity.VideoRequestVO;
import com.chinatower.provider.call.video.client.PresetNewClient;
import com.chinatower.provider.call.video.client.VideoClient;
import com.chinatower.video.api.entity.DoCameraFocusOrZoomReq;
import com.chinatower.video.api.entity.DoPtzCmdsReq;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * video-service-api
 */
@RestController
@Slf4j
@CrossOrigin
@Api(tags = "监控服务接口", description = "监控服务接口")
public class DevicesService implements DevicesAPI {
    @Resource
    private VideoClient videoClient;
    @Resource
    private DevicesDAO devicesDAO;
    @Resource
    private PresetNewClient presetNewClient;
    @Resource
    private com.chinatower.product.api.modules.preset.PresetService presetService;
    @Value("${enableChannelSize}")//需要在配置文件配置
    private int enableChannelSize;
    @Value("${enableCustomerStr}")//需要在配置文件配置
    private String enableCustomerStr;

    /**
     * 5.1.获取通道的录像回放流URL地址
     *
     * @param deviceCode
     * @param channelCode
     * @param map
     * @param request
     * @return
     */
    @Override
    public RespResult getVideoPlaybackUrl(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
        VideoRequestVO videoVO = new VideoRequestVO();
        videoVO.setDeviceCode(deviceCode);
        videoVO.setChannelCode(channelCode);
        videoVO.setCustomerCode(request.getHeader(Constants.CUSTOMER_CODE));
        videoVO.setStreamUuid(request.getHeader("X-StreamUuid"));
        videoVO.setPlugId(request.getHeader("X-PlugId"));
        JSONObject res = this.getVideoPlaybackUrl(videoVO,map);
        return JSON.toJavaObject(res, RespResult.class);
    }

    private JSONObject getVideoPlaybackUrl(VideoRequestVO videoRequestVO, Map map){
        JSONObject resultJson = videoClient.getVideoPlaybackUrl(videoRequestVO.getDeviceCode(),
                videoRequestVO.getChannelCode(), videoRequestVO.getStreamUuid(), videoRequestVO.getOrderCode(),
                videoRequestVO.getCustomerCode(), videoRequestVO.getPlugId(),
                (JSONObject) JSONObject.toJSON(map));
        log.info("========getVideoPlaybackUrl请求报文："+map);
        log.info("========getVideoPlaybackUrl返回报文："+resultJson);
        return resultJson;
    }

    /**
     * 5.2.获取通道实时流URL地址
     *
     * @param deviceCode
     * @param channelCode
     * @param map
     * @param request
     * @return
     */
    @Override
    public RespResult getVideoRealtimeUrl(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
        VideoRequestVO videoVO = new VideoRequestVO();
        videoVO.setDeviceCode(deviceCode);
        videoVO.setChannelCode(channelCode);
        videoVO.setCustomerCode(request.getHeader(Constants.CUSTOMER_CODE));
        videoVO.setStreamUuid(request.getHeader("X-StreamUuid"));
        videoVO.setPlugId(request.getHeader("X-PlugId"));
        JSONObject res = this.getVideoRealtimeUrl(videoVO,map);
        return JSON.toJavaObject(res, RespResult.class);
    }

    private JSONObject getVideoRealtimeUrl(VideoRequestVO videoRequestVO, Map map){
        JSONObject resultJson = videoClient.getVideoRealtimeUrl(videoRequestVO.getDeviceCode(),
                videoRequestVO.getChannelCode(), videoRequestVO.getStreamUuid(), videoRequestVO.getOrderCode(),
                videoRequestVO.getCustomerCode(), videoRequestVO.getPlugId(),
                (JSONObject) JSONObject.toJSON(map));
        log.info("========getVideoRealtimeUrl请求报文："+map);
        log.info("========getVideoRealtimeUrl返回报文："+resultJson);
        return resultJson;
    }

    /**
     * 6.49 批量获取通道实时流URL地址
     * @param list
     * @param request
     * @return
     */
    @Override
    public RespResult getVideoRealtimeUrls(@RequestBody List list, HttpServletRequest request) {
        if(!StringUtils.isEmpty(enableCustomerStr)&&!StringUtils.matches(request.getHeader(Constants.CUSTOMER_CODE), Arrays.asList(enableCustomerStr.split(",")))){
            return RespResult.FAIL("当前客户没有权限", "8888","当前客户没有权限");
        }
        if(list.size()>enableChannelSize){
            return RespResult.FAIL("当前通道数量超过允许的最大值："+enableChannelSize, "8888","当前通道数量超过允许的最大值"+enableChannelSize);
        }
        log.info("========getVideoRealtimeUrls请求报文："+list);
        JSONObject resultJson = videoClient.getVideoRealtimeUrls((JSONObject) JSONObject.toJSON(list));
        log.info("========getVideoRealtimeUrls返回报文："+resultJson);
        return JSON.toJavaObject(resultJson, RespResult.class);
    }

    /**
     * 5.3.开始/结束云台转动
     *
     * @param deviceCode
     * @param channelCode
     * @param doPtzCmdsReq
     * @return
     */
    @Override
    public RespResult doPtzCmds(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody DoPtzCmdsReq doPtzCmdsReq, HttpServletRequest request) {
        String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
        String userCode = request.getHeader(Constants.USER_CODE);
        String appCode = request.getHeader(Constants.APP_CODE);
        log.info("========doPtzCmds入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, videoBaseReq={}",
                deviceCode, channelCode, customerCode, userCode, appCode, doPtzCmdsReq);
        ResultEntity result = presetNewClient.doPtzCmds(deviceCode, channelCode, appCode, customerCode, userCode, doPtzCmdsReq);
        log.info("========doPtzCmds结果：" + result);
        return presetService.parseRespResultNew(result);
    }

    private JSONObject doPtzCmds(VideoRequestVO videoRequestVO, Map map){
        JSONObject resultJson = videoClient.doPtzCmds(videoRequestVO.getDeviceCode(),
                videoRequestVO.getChannelCode(), videoRequestVO.getOrderCode(),
                videoRequestVO.getCustomerCode(), videoRequestVO.getLockToken(),
                (JSONObject) JSONObject.toJSON(map));
        return resultJson;
    }


    /**
     * 5.4.开始/结束聚焦(focus)/缩放(zoom)
     *
     * @param deviceCode
     * @param channelCode
     * @param doCameraFocusOrZoomReq
     * @return
     */
    @Override
    public RespResult doCameraFocusOrZoom(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq, HttpServletRequest request) {
        String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
        String userCode = request.getHeader(Constants.USER_CODE);
        String appCode = request.getHeader(Constants.APP_CODE);
        log.info("========doCameraFocusOrZoom入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, videoBaseReq={}",
                deviceCode, channelCode, customerCode, userCode, appCode, doCameraFocusOrZoomReq);
        ResultEntity result = presetNewClient.doCameraFocusOrZoom(deviceCode, channelCode, appCode, customerCode, userCode, doCameraFocusOrZoomReq);
        log.info("========doCameraFocusOrZoom结果：" + result);
        return presetService.parseRespResultNew(result);
    }

    private JSONObject doCameraFocusOrZoom(VideoRequestVO videoRequestVO, Map map){
        JSONObject resultJson = videoClient.doCameraFocusOrZoom(videoRequestVO.getDeviceCode(),
                videoRequestVO.getChannelCode(), videoRequestVO.getOrderCode(),
                videoRequestVO.getCustomerCode(), videoRequestVO.getLockToken(),
                (JSONObject) JSONObject.toJSON(map));
        return resultJson;
    }

    public ProcessResult doLock(VideoRequestVO videoRequestVO) {
        log.info("4.5.云台加锁申请---请求报文------" + videoRequestVO.toString());
        if (NullProcessUtil.isNull(videoRequestVO.getDeviceCode())) {
            return new ProcessResult(ProcessResult.ERROR, "设备编码不能为空");
        }
        if (NullProcessUtil.isNull(videoRequestVO.getChannelCode())) {
            return new ProcessResult(ProcessResult.ERROR, "通道编码不能为空");
        }
        if (NullProcessUtil.isNull(videoRequestVO.getCustomerCode())) {
            return new ProcessResult(ProcessResult.ERROR, "客户编码不能为空");
        }

        JSONObject resultJson = videoClient.doLock(videoRequestVO.getDeviceCode(), videoRequestVO.getChannelCode(),
                videoRequestVO.getOrderCode(),videoRequestVO.getCustomerCode());
        log.info("4.5.云台加锁申请---返回报文------" + resultJson);
        if (resultJson != null && "0000".equals(resultJson.getString("returnCode"))) {
            String json = JSON.toJSONString(resultJson.get("data"));//map转String
            JSONObject platObj = JSON.parseObject(json);//String转json
            PlatformControlVO platformControlVO = JSON.toJavaObject(platObj, PlatformControlVO.class);
            platformControlVO.setChannelCode(videoRequestVO.getChannelCode());
            platformControlVO.setDeviceCode(videoRequestVO.getDeviceCode());
            platformControlVO.setCustomerCode(videoRequestVO.getCustomerCode());
            platformControlVO.setLockId(devicesDAO.getUUID());
            if (1 == platformControlVO.getLock()) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                platformControlVO.setEndDate(sdf.format(new Date(date.getTime() + platformControlVO.getDuration()*1000)) + "");
                devicesDAO.addPlatformControl(platformControlVO);
            }

            return new ProcessResult(ProcessResult.SUCCESS, "", resultJson.get("data"));
        } else if (NullProcessUtil.isNull(resultJson.getString("returnCode"))){
            return new ProcessResult(ProcessResult.ERROR, "系统开小差了，请稍后再试！");
        } else {
            return new ProcessResult(ProcessResult.ERROR, resultJson.getString("errorMsg"));
        }
    }

    public ProcessResult getToken(VideoRequestVO videoRequestVO){
        PlatformControlVO platformControlVO = new PlatformControlVO();
        platformControlVO.setDeviceCode(videoRequestVO.getDeviceCode());
        platformControlVO.setChannelCode(videoRequestVO.getChannelCode());
        platformControlVO.setCustomerCode(videoRequestVO.getCustomerCode());
        platformControlVO.setEndDate("1");
        platformControlVO.setLock(1);
        List<PlatformControlVO> platList = devicesDAO.queryPlatformControl(platformControlVO);
        if(CollectionUtils.isEmpty(platList)){
            platformControlVO.setLock(0);
            platformControlVO.setEndDate("");
            List<PlatformControlVO> platformList = devicesDAO.queryPlatformControl(platformControlVO);
            if (platformList != null && platformList.size() > 0 &&  //该设备通道已申请，在排队中
                    (platformList.get(0).getLockStatus() == 2 || platformList.get(0).getLockStatus() == 0)) {
                return new ProcessResult(ProcessResult.ERROR, "正在排队中",platformList.get(0));
            } else {
                ProcessResult doLockRes = doLock(videoRequestVO);
                if ("SUCCESS".equals(doLockRes.getResultStat())) {
                    String json = JSON.toJSONString(doLockRes.getData());//map转String
                    JSONObject platObj = JSON.parseObject(json);//String转json
                    PlatformControlVO platVO = JSON.toJavaObject(platObj , PlatformControlVO.class);
                    if (1 == platVO.getLock()) {  //0-排队中；1-加锁成功
                        return new ProcessResult(ProcessResult.SUCCESS, "", platVO.getToken());
                    }else{
                        return new ProcessResult(ProcessResult.ERROR, "正在排队中");
                    }
                }
                return doLockRes;
            }
        }
        return new ProcessResult(ProcessResult.SUCCESS, "", platList.get(0).getToken());
    }
}
