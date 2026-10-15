package com.chinatower.product.api.modules.fallback;

import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.provider.call.video.client.PresetNewClient;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: CallPresetServiceNewClientFallback
 * @Author: wangyt
 * @CreateTime: 2022-09-09  14:28
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class CallPresetServiceNewClientFallback implements FallbackFactory<PresetNewClient> {
    @Override
    public PresetNewClient create(Throwable throwable) {
        return new PresetNewClient(){

            @Override
            public ResultEntity addCameraPreset(String deviceCode, String channelCode, String customerCode, String userCode, String appCode, AddPresetReq addPresetReq) {
                log.info("增加预置点异常");
                ResultEntity resultEntity = new ResultEntity();
                resultEntity.setReturnCode(throwable.getMessage());
                resultEntity.setErrorMsg(throwable.getMessage());
                return resultEntity;
            }

            @Override
            public Result<String> delCameraPreset(String deviceCode, String channelCode, Long presetId, String appCode, String customerCode, String userCode, DelPresetReq delPresetReq) {
                log.info("删除预置点异常");
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result<String> updatePresetInfo(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode , ModPresetReq modPresetReq) {
                log.info("修改预置点异常");
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result<List<QueryPresetListData>> queryPresetList(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, VideoBaseReq videoBaseReq) {
                log.info("查询预置点列表异常");
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result<QueryVideoData> queryVideoList(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
                log.info("查询查询通道的录像列表异常");
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result<PresetData> queryPtzPosition(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, VideoBaseReq videoBaseReq) {
                log.info("查询PTZ位置异常");
                return Result.fail(throwable.getMessage());
            }

            @Override
            public ResultEntity doControlPreset(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, DoControlPresetReq doControlPresetReq) {
                log.info("转到预置点异常");
                ResultEntity resultEntity = new ResultEntity();
                resultEntity.setReturnCode(throwable.getMessage());
                resultEntity.setErrorMsg(throwable.getMessage());
                return resultEntity;
            }

            @Override
            public ResultEntity doPtzCmds(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, DoPtzCmdsReq doPtzCmdsReq) {
                ResultEntity resultEntity = new ResultEntity();
                log.info("开始/结束云台转动异常");
                resultEntity.setReturnCode(throwable.getMessage());
                resultEntity.setErrorMsg(throwable.getMessage());
                return resultEntity;
            }

            @Override
            public ResultEntity doCameraFocusOrZoom(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, DoCameraFocusOrZoomReq doCameraFocusOrZoomReq) {
                ResultEntity resultEntity = new ResultEntity();
                log.info("开始/结束聚焦(focus)/缩放(zoom)异常");
                resultEntity.setReturnCode(throwable.getMessage());
                resultEntity.setErrorMsg(throwable.getMessage());
                return resultEntity;
            }

            @Override
            public Result doPtzLocationCmd(String deviceCode, String channelCode, String appCode, String customerCode, String userCode, DoPtzLocationCmdReq doControlPresetReq) {
                log.info("转到指定PTZ位置异常");
                return Result.fail(throwable.getMessage());
            }
        };
    }
}
