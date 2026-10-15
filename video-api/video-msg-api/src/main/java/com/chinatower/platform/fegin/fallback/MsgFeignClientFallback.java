package com.chinatower.platform.fegin.fallback;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.*;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @auther liuhuanhuan
 * @date 2021/12/16
 */
@Slf4j
@Component
public class MsgFeignClientFallback implements MsgFeignClient {


    @Setter
    private Throwable cause;

    public static final String SEND_MESSAGE_ERROR_STR = "短信发送失败！";
    /**
     * 内部发送短信接口
     *
     * @param smsRecord
     * @return
     */
    @Override
    public Result sendMessage(CommonSmsRequest smsRecord) {
        log.error("内部发送短信接口：服务降级:{}",cause);
        log.error("调用ebike-biz-msg服务发送短信失败，入参：{}",smsRecord.toString());
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result innovationSendMessage(MsgRequestVO request) {
        log.error("innovationSendMessage发送短信接口：服务降级:{}",cause);
        log.error("调用创新平台发送短信失败，入参：{}", JSON.toJSONString(request));
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result sendMail(CommonMailRequest mailRequest) {
        log.error("内部发送邮件接口：服务降级:{}", cause);
        log.error("调用msg服务发送邮件失败，入参：{}", JSON.toJSONString(mailRequest));
        return Result.fail("邮件发送失败！");
    }

    @Override
    public Result batchSendMessage(List<CommonSmsRequest> smsRecords) {
        log.error("内部批量发送短信接口：服务降级:{}",cause);
        log.error("调用msg服务批量发送短信失败，入参：{}",smsRecords.toString());
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result pushNewsByUserId(JgPushRequest pushNews) {
        log.error("极光推送接口：服务降级:{}",cause);
        log.error("调用极光推送信失败，入参：{}",pushNews.toString());
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result sendMessageCode(SmsCodeRequest smsCodeRequest, String dynamic) {
        log.error("内部发送短信接口：服务降级:{}",cause);
        log.error("调用ebike-biz-msg服务发送短信失败，入参：{}",smsCodeRequest.getPhone());
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result sendMessageCheck(SmsCodeRequest smsCodeRequest, String dynamic) {
        log.error("内部批量发送短信接口：服务降级:{}",cause);
        log.error("调用msg服务批量发送短信失败，入参：{}",smsCodeRequest.toString());
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result sendWxMessage(WxPushRequest wxPushRequest) {
        log.error("发送微信公众号告警通知接口：服务降级:{}", cause);
        log.error("发送微信公众号告警通知失败，入参：{}", wxPushRequest.getId());
        return Result.fail("微信公众号告警通知发送失败！");
    }

    @Override
    public Result batchPushNews(List<JgPushRequest> pushNews) {
        log.error("极光推送接口,服务降级:{}", cause);
        log.error("调用极光推送信失败，入参：{}", pushNews.toString());
        return Result.fail("极光推送发送失败！");
    }

    @Override
    public Result checkPhoneCode(String phone, String code, String templateKey, Boolean canRepeatCheck) {
        log.error("校验短信验证码接口：服务降级:{}", cause);
        log.error("调用校验短信验证码接口，入参：phone:{},code:{},templateKey:{},canRepeatCheck:{}", phone, code, templateKey, canRepeatCheck);
        return Result.fail(SEND_MESSAGE_ERROR_STR);
    }

    @Override
    public Result<Map<String, SysMsgSwitch>> queryByUserList(List<MsgSwitchRequest> userList) {
        log.error("查询用户通知状态接口：服务降级:{}",cause);
        log.error("调用msg服务查询用户通知状态失败，入参：{}",userList.toString());
        return Result.fail("查询用户通知状态失败");
    }
}