package com.chinatower.event.asyncTask;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.video.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Component
public class AsyncVideoCommon {

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private SystemFeignClient systemFeignClient;

    //抓视频 15s
    @Async("taskExecutor")
    public void captureVideo(List<VideoDownloadVO> downList){
        try {
            //抓视频开关 0开  1关
            String data ="";
            Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(Constants.CAPTURE_VIDEOS_SWITCH);
            if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
                List<SysDictDataResponse> sysDictDataList = sysResult.getData();
                if (!CollectionUtils.isEmpty(sysDictDataList)) {
                    data = sysResult.getData().get(0).getDictLabel();
                }
            }
            if (!"1".equals(data)){
                if (!CollectionUtils.isEmpty(downList)){
                    log.info("captureVideo入参:{}",JSON.toJSONString(downList));
                    Result<List<VideoDownloadDTO>> result = videoPlayerAPI.downloadVideo(downList);
                    log.info("captureVideo出参:{}",JSON.toJSONString(result));
                    if (result == null || ResultCode.SUCCESS.getCode() != result.getCode()){
                        log.info("设备录像下载失败:{}",JSON.toJSONString(result));
                        throw new Exception("设备录像下载失败");
                    }
                    //停止15s
                    Thread.sleep(15000);
                    Result<List<VideoDownloadDTO>> stopResult = videoPlayerAPI.stopDownloadVideo(downList);
                    log.info("stopDownloadVideo出参:{}",JSON.toJSONString(stopResult));
                    if (stopResult == null || ResultCode.SUCCESS.getCode() != stopResult.getCode()){
                        log.info("停止设备录像下载失败{}",JSON.toJSONString(stopResult));
                        throw new Exception("停止设备录像下载失败");
                    }
                }
            }
        }catch (Exception e){
            log.error("抓视频失败:", e);
        }
    }

}
