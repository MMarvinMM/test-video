package com.chinatower.order.api;


import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.factory.RecorderDeviceApiFallbackFactory;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "recorderDeviceApiService", value = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RecorderDeviceApiFallbackFactory.class)

public interface IRecorderDeviceApiService {

    @PostMapping("/device/recorder/track")
    Result receiveRecorderTrack(@RequestBody String req);

    /**
      * 查询执法记录仪轨迹信息
      * @apiNote   查询执法记录仪轨迹信息
      * @apiVersion  3.142
      * @tag     设备能力
      * @requestDemo
     * {
     *     "deviceCode": "11010500991327000888",
     *     "beginTime": "2024-05-05 16:45:09",
     *     "endTime": "2024-05-05 16:45:09"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      time: 上报事件
     *      longitude: 经度
     *      latitude: 纬度
     *      speed: 速度
     *      direction: 方向
     *      altitude: 海拔
     *      deviceCode: 设备编码
     *      channelCode: 通道编码
     *      accessNode: 接入节点
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307085178,
     *     "data": [
     *         {
     *             "collectionName": "recorder_device_track_202405",
     *             "time": "2024-05-05 16:45:09",
     *             "longitude": "106.486378028",
     *             "latitude": "30.780048633",
     *             "speed": "36.5",
     *             "direction": "东南",
     *             "altitude": "600.2",
     *             "deviceCode": "11010500991327000888",
     *             "channelCode": "18113623664705525888",
     *             "accessNode": "10000000001"
     *         },
     *         {
     *             "time": "2024-05-05 16:45:09",
     *             "longitude": "106.486378028",
     *             "latitude": "30.780048633",
     *             "speed": "36.5",
     *             "direction": "东南",
     *             "altitude": "600.2",
     *             "deviceCode": "11010500991327000888",
     *             "channelCode": "18113623664705525888",
     *             "accessNode": "10000000001"
     *         }
     *     ]
     * }
     */
    @PostMapping("/device/recorder/getRecorderDeviceTrackList")
    Result<List<RecorderDeviceTrackDTO>> getRecorderDeviceTrackList(@RequestBody UavDeviceTrackParam param);
}
