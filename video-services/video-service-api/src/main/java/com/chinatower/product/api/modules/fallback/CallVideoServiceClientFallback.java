package com.chinatower.product.api.modules.fallback;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.provider.call.video.client.VideoClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CallVideoServiceClientFallback implements FallbackFactory<VideoClient> {

    @Override
    public VideoClient create(Throwable cause) {
        return new VideoClient() {
            @Override
            public JSONObject addCruisePatrol(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject delPatrol(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject batchDelPatrol(String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject updatePatrol(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject queryPatrol(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject doPatrol(String deviceCode, String channelCode, String customerCode, String userCode, String lockToken, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject batchStartOrEndPatrol(String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject queryOrderList(JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject doRegister(String customerCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject doUnregister(String customerCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            @Override
            public JSONObject doLock(String deviceCode, String channelCode, String orderCode, String customerCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject doRelease(String deviceCode, String channelCode, String lockToken, String customerCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject queryDeviceList(JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("returnCode","9999");
                jsonObject.put("errorMsg",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject querySites(JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject getVideoPlaybackUrl(String deviceCode, String channelCode, String streamUuid, String orderCode, String customerCode, String plugId, JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject getVideoRealtimeUrl(String deviceCode, String channelCode, String streamUuid, String orderCode, String customerCode, String plugId, JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject getVideoRealtimeUrls(JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject doPtzCmds(String deviceCode, String channelCode, String orderCode, String customerCode, String lockToken, JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject getAlarmInfo(String deviceCode, String channelCode, String alarmId) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }
            @Override
            public JSONObject doCameraFocusOrZoom(String deviceCode, String channelCode, String orderCode, String customerCode, String lockToken, JSONObject json) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1,cause.getMessage());
                jsonObject.put("flag","9999");
                jsonObject.put("desc",cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject addCameraPreset(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject delCameraPreset(String deviceCode, String channelCode, Integer presetId, String customerCode, String userCode, String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject batchDelCameraPreset(int[] presets, String orderCode, String customerCode, String userCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject updatePresetInfo(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject queryPresetList(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject doControlPreset(String deviceCode, String channelCode, String customerCode, String orderCode, String lockToken, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public String queryGuardList(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject.toJSONString();
            }

            @Override
            public String doSetGuard(String deviceCode, String channelCode, String customerCode, String userCode, String orderCode, String lockToken, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject.toJSONString();
            }

            /**
             * 4.2.	获取行政区域树
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject getRegionList(JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            /**
             * 6.3.	查询通道的录像列表
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject queryVideoList(String deviceCode, String channelCode,
                                             String customerCode, String userCode,
                                             String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.15. 查询PTZ位置(绝对位置）
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject queryPtzPosition(String deviceCode, String channelCode, String customerCode, String userCode,
                                               String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.16.	转到指定PTZ位置(绝对位置）
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject doPtzLocationCmd(String deviceCode, String channelCode, String customerCode, String userCode,
                                               String orderCode, String lockToken, JSONObject inJson ) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.17.	查询当前聚焦参数(focus) /缩放(zoom)
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject getCameraFocusOrZoomInfo(String deviceCode, String channelCode, String customerCode, String userCode,
                                                       String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.5.	获取通道抓拍图片URL
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject capturePictUrl(String customerCode, String userCode,
                                             String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.9.	新增通道录像计划
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject addRecordPlan(String deviceCode, String channelCode, String customerCode, String userCode,
                                            String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            /**
             * 6.10.	修改通道录像计划
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject updateRecordPlan(String deviceCode, String channelCode, String customerCode, String userCode,
                                               String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }


            /**
             * 6.11.	删除通道录像计划
             * @param inJson
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject deleteRecordPlan(String deviceCode, String channelCode, String customerCode, String userCode,
                                               String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject batchDeleteRecordPlan(String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject batchStartOrEndRecordPlan(String customerCode, String userCode, String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            /**
             * 6.12.	查询通道录像计划
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/5/11
             * @return
             */
            @Override
            public JSONObject queryRecordPlan(String deviceCode, String channelCode, String customerCode, String userCode,
                                              String orderCode) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            @Override
            public JSONObject synchDeviceAlgorithms(JSONArray json) {

                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }

            /**
             * 6.36.	3D定位
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/7/20
             * @return
             */
            @Override
            public JSONObject thirdLocation(String deviceCode, String channelCode, String customerCode, String userCode,
                                            String orderCode, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }



            /**
             * 6.37.	 获取语音对讲通道
             * @param deviceCode
             * @param channelCode
             * @author: liuxt
             * @date: 2021/7/20
             * @return
             */
            @Override
            public JSONObject getAudioChannel(String deviceCode, String channelCode, String customerCode, String userCode,
                                              String orderCode, String plugId, JSONObject inJson) {
                JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
            }
            
            /**
             * @description: 6.38抓图
             * @param:
             * @return:
             * @author: dengyl@strongdata.com.cn
             * @date: 2021/11/2 10:50
             */
			@Override
			public JSONObject snapshot(String deviceCode, String channelCode, String customerCode, String userCode,
                                       String orderCode, JSONObject inJson) {
				JSONObject jsonObject = new JSONObject();
                log.error(Constants.MESS_1, cause.getMessage());
                jsonObject.put("flag", "9999");
                jsonObject.put("desc", cause.getMessage());
                return jsonObject;
			}

        };
    }
}
