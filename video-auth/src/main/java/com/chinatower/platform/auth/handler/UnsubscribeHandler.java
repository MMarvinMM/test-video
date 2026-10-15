package com.chinatower.platform.auth.handler;

import com.chinatower.platform.auth.project.service.WxService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
@Slf4j
public class UnsubscribeHandler extends AbstractHandler {

    /**
     * 微信公众号推送消息业务层接口
     */
    @Autowired
    private WxService wxService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        String openId = wxMessage.getFromUser();
        log.info("取消关注用户 OPENID: " + openId);
        wxService.updateUnSubscribe(openId);
        return null;
    }

}
