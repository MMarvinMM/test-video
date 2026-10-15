package com.chinatower.platform.fegin.client;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.*;
import com.chinatower.platform.fegin.fallback.MsgFeignClientFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "video-msg",fallback = MsgFeignClientFallback.class)
public interface MsgFeignClient {

    /**
     * 内部发送短信接口
     * @apiNote 内部发送短信接口
     * @apiVersion 10.1
     * @tag 十、信息通知能力
     * @requestDemo
     *{
     *  "phone": "",
     *  "msgType": "2",
     *  "requestMap": "{\"dateTime\":\"2022-09-08 10:54:22\",\"type\":\"测试告警\",\"number\":\"25\"}",
     *  "templateCode": "dafo_otql"
     * }
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "发送短信成功",
     *  "time": 1662615606115
     * }
     * @param smsRecord
     * @return
     */
    @PostMapping("/sms/inSideSendMessage")
    Result sendMessage(CommonSmsRequest smsRecord);

    @PostMapping("/sms/innovationSendMessage")
    Result innovationSendMessage(MsgRequestVO request);

    /**
     * 内部发送邮件接口
     *
     * @param mailRequest 邮件请求
     * @return 发送结果
     */
    @PostMapping("/mail/inSideSendMessage")
    Result sendMail(@Validated @RequestBody CommonMailRequest mailRequest);

    /**
     * 批量发送短信接口
     * @apiNote 内部批量发送短信接口
     * @apiVersion 10.2
     * @tag 十、信息通知能力
     * @requestDemo
     *[{
     *  "phone": "15892333333,15892331111",
     *  "msgType": "2",
     *  "requestMap": "{\"dateTime\":\"2022-09-08 10:54:22\",\"type\":\"测试告警\",\"number\":\"25\"}",
     *  "templateCode": "dafo_otql"
     * },
     * {
     *  "phone": "15892444444",
     *  "msgType": "2",
     *  "requestMap": "{\"dateTime\":\"2022-09-08 10:54:22\",\"type\":\"测试告警\",\"number\":\"25\"}",
     *  "templateCode": "dafo_otql"
     * }]
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "发送短信成功",
     *  "time": 1662615606115
     * }
     * @param   smsRecords
     * @return json
     */
    @ApiOperation(value = "批量发送短信", produces = "application/json")
    @PostMapping("/sms/batchSendMessage")
    Result batchSendMessage(@Validated @RequestBody List<CommonSmsRequest> smsRecords);

    /**
     * 极光推送接口
     * @apiNote 极光推送接口
     * @apiVersion 10.3
     * @tag 十、信息通知能力
     * @requestDemo
     * alias 用户id
     * appVerCode 版本
     * extrasparam 扩展字段
     * industryCode 行业编码
     * msgContent 消息内容
     * msgTitle 消息标题
     * notificationTitle 通知标题
     * msgType 极光消息类型  0-不提醒消息  1-通知类消息
     * sound 声音文件
     * type 1-告警、超期提醒 2-行业资讯 3-通知公告 4-其他
     *
     *{
     *  "alias": "f689c983880c4440a123e794e40e31aa",    //用户id
     *  "appVerCode": "",
     *  "extrasparam": "",
     *  "industryCode": "600002",
     *  "msgContent": "",
     *  "msgTitle": "物联设备告警",
     *  "notificationTitle": "待研判",
     *  "msgType": "1",         //极光消息类型  0-不提醒消息  1-通知类消息
     *  "sound": "sound",    //声音文件名称
     *  "type": "1"    //1-告警、超期提醒 2-行业资讯 3-通知公告 4-其他
     *     }
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "发送成功",
     *  "time": 1662615606115
     * }
     * @param pushNews
     */
    @PostMapping("/bussiness/jush/checkShortMessage")
    Result pushNewsByUserId(JgPushRequest pushNews);

    /**
     * 发送短信校验码
     * @apiNote 发送短信校验码
     * @apiVersion 10.4
     * @tag 十、信息通知能力
     * @requestDemo
     *{
     *  "phone": "",
     *  "msgType": "1",
     *  "aesPhone":"",
     *  "userName": "zhangsan",
     *  "templateKey": "dafo_otql"
     * }
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "发送短信成功",
     *  "time": 1662615606115
     * }
     * @param smsCodeRequest
     * @return Result
     */
    @PostMapping("base/sms/sendMessage")
    Result sendMessageCode(@Validated @RequestBody SmsCodeRequest smsCodeRequest, @RequestHeader(name = "dynamic") String dynamic);


    /**
     * 发送短信校验码
     * @param smsCodeRequest
     * @return
     */
    @PostMapping("base/sms/sendMessageCheck")
    Result sendMessageCheck(@Validated @RequestBody SmsCodeRequest smsCodeRequest, @RequestHeader(name = "dynamic") String dynamic);

    /**
     * 发送微信公众号告警通知
     * @apiNote 发送微信公众号告警通知
     * @apiVersion 10.5
     * @tag 十、信息通知能力
     * @requestDemo
     * id 消息id
     * linkUrl 跳转链接
     * extrasparam 扩展字段
     *  thing8 事件类型
     *  time3 事件
     *  phrase9 事件状态
     *  thing4 告警地点
     *{
     *     "id": "202309141220358071774154",
     *      "linkUrl": null,
     *      "extrasparam" : {
     *          "thing8" : "高速速度过低-雷达默认6",
     *          "time3": "2023-09-14 13:49:30",
     *          "phrase9": "待受理",
     *          "thing4": "山西省晋城市沁水县端氏镇花沟"
     *      },
     *      "userIds": ["070185f505b54c68883f70d5e184da72", "0b23a19f5668476388115805213d28d"]
     *}
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "推送公众号通知成功",
     *  "time": 1662615606115
     * }
     * @param wxPushRequest
     */
    @PostMapping("/wxmsg/sendWxMessage")
    Result sendWxMessage(@Validated @RequestBody WxPushRequest wxPushRequest);


    /**
     * 极光推送批量接口
     * @apiNote 极光推送批量接口
     * @apiVersion 10.6
     * @tag 十、信息通知能力
     * @requestDemo
     * alias 用户id
     * appVerCode 版本
     * extrasparam 扩展字段
     * industryCode 行业编码
     * msgContent 消息内容
     * msgTitle 消息标题
     * notificationTitle 通知标题
     * msgType 极光消息类型  0-不提醒消息  1-通知类消息
     * sound 声音文件
     * type 1-告警、超期提醒 2-行业资讯 3-通知公告 4-其他
     *[{
     *  "alias": "f689c983880c4440a123e794e40e31aa",    //用户id
     *  "appVerCode": "",
     *  "extrasparam": "",
     *  "industryCode": "600002",
     *  "msgContent": "",
     *  "msgTitle": "物联设备告警",
     *  "notificationTitle": "待研判",
     *  "msgType": "1",         //极光消息类型  0-不提醒消息  1-通知类消息
     *  "sound": "sound",    //声音文件名称
     *  "type": "1"    //1-告警、超期提醒 2-行业资讯 3-通知公告 4-其他
     *     }
     *]
     * @responseDemo
     *{
     *  "code": 200,
     *  "msg": "发送成功",
     *  "time": 1662615606115
     * }
     * @param pushNews
     */
    @PostMapping("/bussiness/jush/batchPushNews")
    Result batchPushNews(@RequestBody List<JgPushRequest> pushNews);

    /**
     * 校验短信验证码
     * @param phone 手机
     * @param code 验证码
     * @param templateKey 短信模板key(内网发送为空)
     * @param canRepeatCheck 是否可以重复校验
     * @return 校验结果
     */
    @PostMapping("base/sms/checkPhoneCode")
    Result checkPhoneCode(@RequestParam String phone,@RequestParam String code, @RequestParam(required = false)String templateKey,@RequestParam(required = false) Boolean canRepeatCheck);


    /**
     * 查询用户列表中各通知开关状态
     * @apiNote 查询用户列表中各通知开关状态
     * @apiVersion 10.7
     * @tag 十、信息通知能力
     * @requestDemo
     * userId 用户id
     * industryCode 行业编码
     *[
     *     {
     *         "userId": "47610796b99b4523981a351f0e401044",
     *         "industryCode": "000000"
     *     },
     *     {
     *         "userId": "12312232331",
     *         "industryCode": "000000"
     *     }
     * ]
     * @responseDemo
     * data map结构返回
     *  key 用户id
     *  value
     *      userId 用户id
     *      alarmOverdueSwitch 超期提醒开关  1-开启 0-关闭
     *      alarmSwitch 铁塔账号使用 运营研判开关  1-开启 0-关闭
     *      noticeSwitch 通知公告/行业资讯提醒开关  1-开启 0-关闭
     *      otherSwitch 其他提醒开关  1-开启 0-关闭
     *      industryCode 行业
     *      tenantId 租户id
     *
     *{
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1683193701334,
     *     "data": {
     *         "47610796b99b4523981a351f0e401044_000000": {		//key:userId_industryCode
     *             "id": null,
     *             "userId": "47610796b99b4523981a351f0e401044",
     * "alarmSwitch": 1,			//铁塔账号使用 运营研判开关  1-开启 0-关闭
     *             "alarmOverdueSwitch": 1,	//超期提醒开关  1-开启 0-关闭
     *             "noticeSwitch": 1,			//通知公告/行业资讯提醒开关  1-开启 0-关闭
     *             "otherSwitch": 1,			//其他提醒开关  1-开启 0-关闭
     *             "deleteFlag": null,
     *             "industryCode": "000000",
     *             "tenantId": "122035807",
     *             "createBy": null,
     *             "createTime": null,
     *             "updateBy": null,
     *             "updateTime": null
     *         },
     *         "12312232331_000000": {
     *             "id": null,
     *             "userId": "12312232331",
     *             "alarmOverdueSwitch": 1,
     *             "noticeSwitch": 1,
     *             "otherSwitch": 1,
     *             "deleteFlag": null,
     *             "industryCode": "000000",
     *             "tenantId": null,
     *             "createBy": null,
     *             "createTime": null,
     *             "updateBy": null,
     *             "updateTime": null
     *         }
     *     }
     * }
     */
    @PostMapping("/switch/queryByUserList")
    Result<Map<String, SysMsgSwitch>> queryByUserList(@RequestBody List<MsgSwitchRequest> userList);
}
