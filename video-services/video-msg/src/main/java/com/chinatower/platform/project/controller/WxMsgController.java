package com.chinatower.platform.project.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.request.WxPushRequest;
import com.chinatower.platform.project.service.WxMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-msg
 * @Description: 微信公众号推送conttroller
 * @Author: 朱君
 * @Date: 2022/11/15
 **/
@Api(tags = "微信公众号推送接口")
@Slf4j
@RestController
@RequestMapping("/wxmsg")
public class WxMsgController extends TowerBaseController{

    /**
     * 微信公众号推送消息业务层接口
     */
    @Autowired
    private WxMsgService wxMsgService;

    /**
     * 发送微信公众号告警通知
     * @param @return
     * @return json
     * @throws
     * @Description: 发送微信公众号告警通知
     */
    @ApiOperation(value = "发送微信公众号告警通知", produces = "application/json")
    @PostMapping("/sendWxMessage")
    public Result sendWxMessage(@Validated @RequestBody WxPushRequest wxPushRequest) {
        try {
            return wxMsgService.sendEventMessage(wxPushRequest);
        } catch (Exception e) {
            log.error("发送微信公众号告警通知异常");
            log.error(e.getMessage(), e);
            return Result.fail("发送微信公众号告警通知失败，请联系管理员");
        }
    }

    /**
     * 批量消息发送微信公众号告警通知
     * @param @return
     * @return json
     * @throws
     * @Description: 批量消息发送微信公众号告警通知
     */
    @ApiOperation(value = "批量消息发送微信公众号告警通知", produces = "application/json")
    @PostMapping("/sendMultipleWxMessage")
    public Result sendMultipleWxMessage(@Validated @RequestBody List<WxPushRequest> wxPushRequests) {
        try {
            return wxMsgService.sendMultipleWxMessage(wxPushRequests);
        } catch (Exception e) {
            log.error("批量消息发送微信公众号告警通知异常");
            log.error(e.getMessage(), e);
            return Result.fail("批量消息发送微信公众号告警通知失败，请联系管理员");
        }
    }

}
