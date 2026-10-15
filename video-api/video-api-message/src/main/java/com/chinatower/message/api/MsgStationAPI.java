package com.chinatower.message.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.factory.MsgStationFallbackFactory;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.message.api.model.SendPresetArrivalNoticeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "msgStationAPI",
        value = ServiceNameConstants.MESSAGE_SOCKET_SERVICE,
        fallbackFactory = MsgStationFallbackFactory.class)
public interface MsgStationAPI {


    /**
     *  RPC调用-消息推送
     * @apiNote 内部消息socket单条推送
     * @apiVersion 12.1
     * @tag 十二、消息通知能力
     * @requestDemo
     * orderStatus 告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
     * msgId 消息ID
     * userId 用户ID
     * notifyType 通知类型:1系统提醒
     * notifyTitle 通知标题
     * address 地址
     * appVerCode 版本
     * alarmFlag 告警标识:1告警
     * industryCode 行业
     * soundCloseType 声音关闭方式 1：自动关闭，2：手动关闭
     * soundDuration 声音时长
     * soundId 声音ID
     *{
     * "orderStatus": "1",
     * "msgId": "2023020370001536715231240",
     * "userId": "6982c2ea96984c80aa84dc3761743c04",
     * "notifyType": "1",
     * "notifyTitle": "物联设备告警",
     * "address": "四川省内江市东兴区石子镇天星堰",
     * "appVerCode": "1.0",
     * "alarmFlag": "1",
     * "showType": "0",
     * "industryCode": "100000",
     * "soundCloseType": "2",
     * "soundDuration": "10",
     * "soundId": "63"
     * }
     * @responseDemo
     * {
     * "code": 200,
     * "msg": "操作成功！",
     * "time": 1675394281645
     * }
     * @param msg
     * @return
     */
    @PostMapping("/InternalForwardingMsg/sendMsg2TargetService")
    Result sendMsg2TargetService(MsgBody msg);

    /**
     *  RPC调用-消息批量推送
     * @apiNote 内部消息socket批量推送
     * @apiVersion 12.2
     * @tag 十二、消息通知能力
     * @requestDemo
     * orderStatus 告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
     * msgId 消息ID
     * userId 用户ID
     * notifyType 通知类型:1系统提醒
     * notifyTitle 通知标题
     * address 地址
     * appVerCode 版本
     * alarmFlag 告警标识:1告警
     * industryCode 行业
     * soundCloseType 声音关闭方式 1：自动关闭，2：手动关闭
     * soundDuration 声音时长
     * soundId 声音ID
     *[{
     * "orderStatus": "1",
     * "msgId": "2023020370001536715231240",
     * "userId": "6982c2ea96984c80aa84dc3761743c04",
     * "notifyType": "1",
     * "notifyTitle": "物联设备告警",
     * "address": "四川省内江市东兴区石子镇天星堰",
     * "appVerCode": "1.0",
     * "alarmFlag": "1",
     * "showType": "0",
     * "industryCode": "100000",
     * "soundCloseType": "2",
     * "soundDuration": "10",
     * "soundId": "63"
     * }]
     * @responseDemo
     * {
     * "code": 200,
     * "msg": "操作成功！",
     * "time": 1675394281645
     * }
     * @param msg
     * @return
     */
    @PostMapping("/InternalForwardingMsg/sendMsg2TargetServiceBatch")
    Result sendMsg2TargetServiceBatch(@RequestBody List<MsgBody> msg);

    @PostMapping("/InternalForwardingMsg/sendPresetArrivalNoticeBatch")
    Result sendPresetArrivalNoticeBatch(@RequestBody List<SendPresetArrivalNoticeVO> msg);

}
