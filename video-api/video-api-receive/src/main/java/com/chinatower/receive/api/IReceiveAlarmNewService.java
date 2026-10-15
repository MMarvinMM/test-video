package com.chinatower.receive.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.receive.api.factory.ReceiveAlarmNewFallbackFactory;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.model.TranslnglatReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(contextId = "receiveAlarmNewService", value = ServiceNameConstants.RECEIVE_SERVICE, fallbackFactory = ReceiveAlarmNewFallbackFactory.class)
public interface IReceiveAlarmNewService {

    /**
     * 物联设备上报告警接口
     * @apiNote 物联设备上报告警接口 详细信息
     * @apiVersion 1.83
     * @tag 一、告警事件能力
     * @requestDemo {[{"alarmTime":"2023-08-09 22:49:03","deviceCode":"1234","deviceName":"111","alarmCode":"111","importanceLevel":"1","alarmBody":"111","monitor":"{"so2":"111"}"}]}
     * @responseDemo {"code": 200,"msg": "接收告警成功","time": 1673254838741,"data": []}
     * @param receiveIotAlarmVOList 接收告警信息集合
     * @return Result
     */
    @PostMapping(value = "/alarm/receive/receiveIotAlarm")
    Result receiveIotAlarm(@RequestBody List<ReceiveIotAlarmVO> receiveIotAlarmVOList);

    /**
     * 卫片告警上报
     */
    @PostMapping(value = "/alarm/receive/receiveRegionAlarm")
    Result receiveRegionAlarm(@RequestBody List<ReceiveRegionAlarmDto> receiveIotAlarmVOList);

    /**
     * 无人机告警上报
     */
    @PostMapping(value = "/alarm/receive/receiveUavAlarm")
    Result receiveUavAlarm(@RequestBody ReceiveUavAlarmVO receiveUavAlarmVO);


    /**
     * 第三方告警上报
     */
    @PostMapping("alarm/receive/addThirdAlarmOrder")
    Result addThirdAlarmOrder(@RequestBody List<ReceiveAlarmVO> receiveAlarmVO);
    /**
     * 新增人工上报/一键告警
     * @apiNote 新增人工上报/一键告警接口
     * @apiVersion 1.84
     * @tag 一、告警事件能力
     * @requestDemo {"warningSource":"3","alarmBody":"扬尘粉尘监控告警","warningTypeId":"1010105","emergencyLevel":"","alarmTime":"2023-06-07 14:08:28","provinceId":"110000","cityId":"110100","countyId":"110108","address":"北京市北京市海淀区四季青镇常润路中国铁塔产业园","longitude":"116.266446","latitude":"39.956909","description":"发生扬尘粉尘监控告警123","remark":"","imgs":[{"uid":1686118106211,"type":"image/png","name":"铁塔产业园区-车库入口_0-20230607140825.png",
     * "url":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/1.0/5198cd91c73f929d0547ad6cd49038ff/289ea5add1b93e70854e7b23137e1d5c/c5b8d89546fc4dca8ecd65874d91432d.png?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1686118709&Signature=xh2x2DBXH6I5sfhVySF5vjr/mfU%3D",
     * "fileName":"铁塔产业园区-车库入口_0-20230607140825.png","tagPictureUrl":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/1.0/5198cd91c73f929d0547ad6cd49038ff/289ea5add1b93e70854e7b23137e1d5c/c5b8d89546fc4dca8ecd65874d91432d.png?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1686118709&Signature=xh2x2DBXH6I5sfhVySF5vjr/mfU%3D","status":"success"}],
     * "videos":[],"deviceCode":"11010800831327000004","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","importanceLevel":"1"}
     * @responseDemo {"code":200,"msg":"保存成功","time":1685585958773}
     * @param receiveAlarmVO 入参
     * @return Result
     */
    @PostMapping("alarm/receive/orderForTrade/addAlarmOrder")
    Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO);

    /**
     * 获取告警摄像机角度
     * @apiNote 获取告警摄像机角度
     * @apiVersion 4.1.36
     * @param req
     * @return
     */
    @PostMapping("alarm/receive/getAngleByDeviceParam")
    Result getAngleByDeviceParam(@RequestBody TranslnglatReq req);

    /**
     * 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
     * @apiNote 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
     * @apiVersion 1.85
     * @tag 一、告警事件能力
     * @requestDemo {"eventId":"910","data":{"alarmBody":"摄像机告警测试测试20251557","msgType":"0","importanceLevel":"1","alarmTime":"2024-12-25 17:10:10","latitude":"","alarmArea":[{"alarmAreaId":"11111","confidenceLevel":0.6884647,"alarmPointList":[{"percentY":"0.665634537","alarmAreaId":"f6285e7a56f6487596f2bd0c1c35371d","percentX":"0.6345653565","alarmPointId":"4"},{"percentY":"0.5435343456","alarmAreaId":"f6285e7a56f6487596f2bd0c1c35371d","percentX":"0.7435346546","alarmPointId":"5"},{"percentY":"0.34353538","alarmAreaId":"f6285e7a56f6487596f2bd0c1c35371d","percentX":"0.843634545","alarmPointId":"6"},{"percentY":"0.334543639","alarmAreaId":"f6285e7a56f6487596f2bd0c1c35371d","percentX":"0.44535343","alarmPointId":"7"}]}],"zoom":"3.0","pan":"2927.0","compConfidenceLevel":"","deviceCode":"11010800831327000003","tilt":"54.0","alarmCode":"0719sxj","longitude":"","extendField":[{"alarmAreaId":"11111","attrList":[{"attrType":"objectId","attrValue":"11111"},{"attrType":"objectId","attrValue":"22222"},{"attrType":"objectId","attrValue":"33333"},{"attrType":"objectTime","attrValue":"601"},{"attrType":"objectTime","attrValue":"602"},{"attrType":"objectTime","attrValue":"603"},{"attrType":"carNumber","attrValue":"京B5678X"},{"attrType":"carType","attrValue":"小轿车"},{"attrType":"plateColor","attrValue":"蓝"},{"attrType":"plateColor","attrValue":"红"},{"attrType":"plateColor","attrValue":"绿"},{"attrType":"objectCount","attrValue":"10"}]}],"deviceName":"铁塔产业园-12号楼角落-大华245","algorithmCode":"ZGTT-1010100001","alarmType":"1010100","alarmVideoUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/video/KLYR-1212102001_7f10bda0ba094caf9bbdb5fc671176a48c9b2e4da43f4025a7f5f57a61823027_976875.mp4","alarmSource":"4","channelName":"铁塔产业园-12号楼角落-大华_0","originalPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/KLYR-1212102001_7f10bda0ba094caf9bbdb5fc671176a48c9b2e4da43f4025a7f5f57a61823027_01_976.jpg","alarmPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/KLYR-1212102001_7f10bda0ba094caf9bbdb5fc671176a48c9b2e4da43f4025a7f5f57a61823027_02_976.jpg","taskId":"eb3e90d30d8344ac8efcaae7a3d3dde9","channelCode":"11010800001310000051","isSecondJudge":1,"secondJudgeResult":0,"noticeResource":2,"msgId":"802db8969a8e4beaab287f87ce345e6a"}}
     * @responseDemo {"code":200,"msg":"接收告警成功","time":1735124006927}
     * @param req 接受告警入参
     * @return Result
     */
    @PostMapping("alarm/receive/receiveAlarm")
    Result receiveAlarm(@RequestBody ReceiveAlarmVOReq req);

    /**
     * 接收卫星告警
     *
     * @param receiveSatelliteAlarmVos
     * @return
     */
    @PostMapping("/alarm/receive/receiveSatelliteAlarmKafka")
    Result receiveSatelliteAlarmKafka(@RequestBody List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos);

}
