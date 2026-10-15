package com.chinatower.product.api.modules.videosquare.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.product.api.modules.videosquare.api.VideoSquareAPI;
import com.chinatower.product.api.modules.videosquare.entity.VideoRequestVO;
import com.chinatower.product.api.modules.videosquare.mapper.VideoSquareDao;
import com.chinatower.provider.call.video.client.VideoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * video-service-api
 * @ClassName: VideoSquareService
 * @Description: 视频广场相关方法
 * @Author: zhazoj
 * @Date: 2021-05-06 16:04
 **/
@Slf4j
@RestController
public class VideoSquareService implements VideoSquareAPI {

    @Autowired(required = false)
    VideoClient videoClient;

    @Autowired(required = false)
    VideoSquareDao videoSquareDao;


    /**
     * 4.5.	云台加锁申请
     * @param videoRequestVO
     * @return
     */
    @Override
    public ProcessResult doLock(@RequestBody VideoRequestVO videoRequestVO) {
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
            return new ProcessResult(ProcessResult.SUCCESS, "", resultJson.get("data"));
        } else if (NullProcessUtil.isNull(resultJson.getString("returnCode"))){
            return new ProcessResult(ProcessResult.ERROR, "系统开小差了，请稍后再试！");
        } else {
            return new ProcessResult(ProcessResult.ERROR, resultJson.getString("errorMsg"));
        }
    }


}
