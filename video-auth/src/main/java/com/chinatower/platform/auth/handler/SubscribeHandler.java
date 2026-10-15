package com.chinatower.platform.auth.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.platform.auth.builder.TextBuilder;
import com.chinatower.platform.auth.config.WxProperties;
import com.chinatower.platform.auth.model.WxLoginVo;
import com.chinatower.platform.auth.project.entity.WxBttomMenuEntity;
import com.chinatower.platform.auth.project.service.WxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
@AllArgsConstructor
@Slf4j
public class SubscribeHandler extends AbstractHandler {

    // 配置参数
    private final WxProperties properties;

    @Autowired
    private WxService wxService;

    @Autowired
    private WxMpService wxMpService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        log.info("新关注用户 OPENID: {}", wxMessage.getFromUser());

        // 获取微信用户基本信息
        try {
            WxMpUser userWxInfo = weixinService.getUserService()
                .userInfo(wxMessage.getFromUser(), null);
            if (userWxInfo != null) {
                WxLoginVo wxVo = new WxLoginVo();
                wxVo.setUnionId(userWxInfo.getUnionId());
                wxVo.setOpenId(userWxInfo.getOpenId());
                wxVo.setNickName(userWxInfo.getNickname());
                // 判断微信用户是否存在
                int hasUser = wxService.getWxUserCount(wxVo);
                if(hasUser > 0){
                    wxService.updateWxUser(wxVo);
                }else{
                    wxService.insertWxUser(wxVo);
                }
            }
        } catch (WxErrorException e) {
            if (e.getError().getErrorCode() == 48001) {
                this.logger.info("该公众号没有获取用户信息权限！");
            }
            log.error(e.getMessage());
        }


        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = this.handleSpecial(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }


        String mainUrl = this.getWelComeUrl();
        /*String welcomeMsg = "感谢您关注铁塔视联公众号。请点击下方蓝字登录绑定用户信息，以便接收告警消息\n\n<a href=\""
                + mainUrl
                + "\">点击此处进行登录</a>";*/
        String welcomeMsg = "感谢您关注铁塔视联公众号。请点击下方蓝字登录绑定用户信息，以便接收告警消息\n\n"
        + "<a data-miniprogram-appid=\""
                + properties.getMiniAppId()
                + "\" data-miniprogram-path=\""
                + properties.getMiniAppEventUrl()
                + "\" href=\"\" data-miniprogram-type=\"text\">点击此处进行登录</a>";

        try {
            return new TextBuilder().build(welcomeMsg, wxMessage, weixinService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    public String getWelComeUrl() {
        WxBttomMenuEntity entity = wxService.getOne(new LambdaQueryWrapper<WxBttomMenuEntity>()
                .eq(WxBttomMenuEntity::getMenuValue, "main"));
        WxOAuth2Service oAuth2Service = this.wxMpService.getOAuth2Service();
        // 构建授权url
        return oAuth2Service.buildAuthorizationUrl(properties.getServerHost() + entity.getLinkUrl(),
                WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) throws Exception {
        //TODO
        return null;
    }

}
