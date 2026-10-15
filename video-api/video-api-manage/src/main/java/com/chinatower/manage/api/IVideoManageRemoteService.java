package com.chinatower.manage.api;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.manage.api.factory.VideoManageRemoteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "videoManageRemoteService", value = "video-manage", fallbackFactory = VideoManageRemoteFallbackFactory.class)
public interface IVideoManageRemoteService {

    @PostMapping("/screenDataConfig/addConfig")
    public Result addConfig(@RequestBody List<ScreenDataConfigVO> screenDataConfigList);


    @PostMapping("/screenDataConfig/editThirdPartyAlarmConfig")
    public Result editThirdPartyAlarmConfig(@RequestBody List<ScreenDataConfigVO> screenDataConfigList);


    /**
     * 获取大屏配置数据
     * @apiNote 11.6 获取大屏配置数据
     * @apiVersion 11.6
     * @tag 十一、数据能力
     * @requestDemo
     * {"screenId":"123","screenType":"2"}
     * @responseDemo
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1737452008709,
     *     "data": {
     *         "id": "大屏配置id",
     *         "tenantId": "客户id",
     *         "industryCode": "行业编码",
     *         "screenId": "大屏id",
     *         "screenType": "大屏类型",
     *         "isCustomize": "是否开启自定义",
     *         "cameraCodeList": [
     *             "摄像机编码集合"
     *         ],
     *         "iotCodeList": [
     *             "物联设备编码集合"
     *         ],
     *         "radarCodeList": [
     *             "雷达编码集合"
     *         ],
     *         "speakerCodeList": [
     *             "喇叭编码集合"
     *         ],
     *         "uavCodeList": [
     *             "无人机编码集合"
     *         ],
     *         "recorderCodeList": [
     *             "执法记录仪编码集合"
     *         ],
     *         "gridCodeList": [
     *             "网格id集合"
     *         ],
     *         "resourcesCodeList": [
     *             "资源id集合"
     *         ],
     *         "alarmTypeList": [
     *             {
     *                 "dataType": "9",
     *                 "code": "告警编码",
     *                 "source": "告警来源"
     *             }
     *         ],
     *         "gridKeeperList": [
     *             {
     *                 "dataType": "7",
     *                 "code": "网格员id",
     *                 "source": "网格员父级id"
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/screenDataConfig/selectConfigById")
    public Result<ScreenDataConfigVO> selectConfigById(@RequestBody ScreenDataConfigVO screenDataConfig);
}
